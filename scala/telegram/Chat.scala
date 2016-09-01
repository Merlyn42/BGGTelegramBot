package telegram

import akka.actor.Actor
import data.Score


class Chat extends Actor {
  def receive = {
    case Score(name)=>
      println("Score request received")
    case _ =>
      println("Unknown Request")
  }
  
}