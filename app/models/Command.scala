package models

trait Event

trait Command {
  def origin : CommandOrigin
}

trait CommandOrigin {
  def logData : String
}

case class HttpCommandOrigin(request:play.api.mvc.RequestHeader) extends CommandOrigin {
  def logData = request.uri
}

case class RegisterUserCommand(username:String, email:String, origin:CommandOrigin) extends Command

case class UserRegisteredEvent(userId:String) extends Event