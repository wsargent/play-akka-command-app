package actors

import akka.actor.{ActorLogging, Actor}
import models._

class UserCommandHandler extends Actor with ActorLogging {
  def receive = {
    case c:Command =>
      log.info("successful! command from url " + c.origin.logData)
      sender ! UserRegisteredEvent("1337")
    case _ =>
      log.error("error, should only receive commands")

  }
}
