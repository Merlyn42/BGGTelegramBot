package root
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Terminated



object Program {
    def main(args: Array[String]): Unit = {
    val system = ActorSystem("BGGBot")
    val a = system.actorOf(Props[BGGBot], "bggbot")
    }
}