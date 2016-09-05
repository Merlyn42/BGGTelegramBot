package telegram.gateway

import scala.collection.mutable
import akka.actor.ActorRef
import info.mukel.telegrambot4s._
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.methods._
import info.mukel.telegrambot4s.models._
import info.mukel.telegrambot4s.Implicits._
import org.json4s._
import scala.concurrent._
import akka.actor.actorRef2Scala
import data.Score
import telegram.Telegram

 class TelegramGateway(actor:ActorRef) extends TelegramBot
               with Polling with Commands {

    def token = "216670754:AAFfOlcpiQ9fGwxAP9CvSz0FyfApp8cNaSU"
    println("Starting telegramGateway")
    on("/score") { implicit msg => args =>
      val message = Telegram.Receive(msg.chat.id,"score",args)
      actor ! message
    }
    
    private val commands = mutable.HashMap[String, (Int, Seq[String]) => Unit]()
    
   
    def send(chatID:Long,message:String) = {
      println("sending \""+message+"\" to "+chatID)
      println(api.request(SendMessage(chatID,message)))
    }
    

  }


