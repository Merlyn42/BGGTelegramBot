package telegram

import akka.actor.Actor
import akka.actor.ActorSelection.toScala
import data.Request
import gateway.TelegramGateway
import akka.actor.Props


object Telegram{
  case class Send(chatID:Long,message:String)
  case class Receive(chatID:Long,command:String, args:Seq[String])
}

class Telegram extends Actor {
  
  val gateway = new TelegramGateway(self)
  
  override def preStart(): Unit = {
    gateway.run()
  }
  
  def receive = {
    case Telegram.Send(id,m) => {
      gateway.send(id, m)
      
      }
    case Telegram.Receive(id,command,args) => {
      val request = command match {
        case "score" => data.Score(args.reduceOption(_ +" "+_).getOrElse(""))
      }
      val chat = context.actorOf(Props(classOf[Chat],id,self))
      chat ! request
    }
  }
  
}