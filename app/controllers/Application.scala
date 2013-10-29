package controllers

import play.api.mvc._

object Application extends Controller {
  import play.api.data._
  import play.api.data.Forms._
  import models._

  case class Register(username:String, email:String)

  val registerForm = Form(
    mapping("username" -> nonEmptyText, "email" -> email)(Register.apply)(Register.unapply)
  )

  def index() = Action {
    Ok(views.html.index(registerForm))
  }

  def registered = Action {
    Ok("You are registered!")
  }

  def register = Action.async { implicit request =>
    registerForm.bindFromRequest.fold(
      hasErrors = { errors =>
        import scala.concurrent.Future
        Future.successful {
          BadRequest(views.html.index(errors))
        }
      },
      success = { registerData =>
        import akka.pattern.ask // provides ?
        import play.api.Play.current
        import akka.util.Timeout
        import scala.concurrent.duration._
        import play.api.libs.concurrent.Akka

        val system = Akka.system
        import system.dispatcher // The ExecutionContext that will be used

        val handler = system.actorSelection("/user/userCommandHandler")
        implicit val timeout = Timeout(5 seconds) // needed for `?` below

        val origin = HttpCommandOrigin(request)
        val username = registerData.username
        val email = registerData.email
        for {
          event <- (handler ? RegisterUserCommand(username, email, origin))
        } yield {
          event match {
            case e:UserRegisteredEvent =>
              Redirect(routes.Application.registered)
            case _ =>
              BadRequest("Invalid registration")
          }
        }
      }
    )
  }
}