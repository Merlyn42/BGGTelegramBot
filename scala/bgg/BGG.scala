package bgg

import akka.actor.Actor

class BGG extends Actor {
  def receive = {
    case Termination
  }
}