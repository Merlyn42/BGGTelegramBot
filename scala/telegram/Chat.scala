package telegram

import akka.actor.Actor
import data.Score
import bgg.BGG
import akka.actor.Props
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorRef

class Chat(chatID:Long,parent:ActorRef) extends Actor {
  
  implicit val timeout = Timeout(5 seconds)
  def receive = {
    case score:Score=>
      println("scoring Request")
      val bgg = context.actorOf(Props(classOf[BGG],self), "bgg")
      bgg ! score
    case s:String =>
      println("received a string")
      parent ! Telegram.Send(chatID,s)
      context.stop(self)
    case _ =>
      println("Unknown Request")
  }
  
}