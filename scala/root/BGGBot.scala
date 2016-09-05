package root

import akka.actor.Actor
import data.Request
import akka.actor.ActorSelection.toScala
import telegram.Telegram
import akka.actor.Props


class BGGBot extends Actor {
  
  override def preStart(): Unit = {
    val tel = context.actorOf(Props[Telegram], "telegram")
  }

  def receive = {
    case x => {
      println(x.toString())
    }
    
  }
  
}