import actors.UserCommandHandler
import akka.actor.Props
import play.api.libs.concurrent.Akka
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    implicit val a = app
    Akka.system.actorOf(Props[UserCommandHandler], name = "userCommandHandler")
  }
}
