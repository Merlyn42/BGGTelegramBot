package root

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._
import akka.http.scaladsl.Http
import akka.actor.Actor
import akka.actor.Terminated
import data.Score
import data.Request
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.ActorMaterializerSettings
import akka.util.ByteString
import akka.actor.ActorSystem
import scala.concurrent.duration._
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.xml.NodeSeq
import akka.util.Timeout
import HttpMethods._
import akka.http.scaladsl.client.RequestBuilding
import scala.concurrent.Await
import java.nio.charset.Charset
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import bgg.SearchResult
import bgg.GameData
import scala.xml.Elem
import scala.xml.Node

object Test {
  
  implicit val system = ActorSystem("test")
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(system))
  implicit val timeout = Timeout(5 seconds)
 
  val http = Http()
    def main(args: Array[String]): Unit = {
    /*val params = Map("search" -> "agricola", "exact" -> "1")   
    val request = RequestBuilding.Get(Uri("http://www.boardgamegeek.com/xmlapi/search").withQuery(Query(params)))
    val respF = Http().singleRequest(request)
    val searchResultF = respF.flatMap { 
      x => Unmarshal(x.entity).to[NodeSeq].map{
        xml => val games = xml \ "boardgame"
         val data =  games.map { case g => println("a test"+g) 
            SearchResult((g \ "name").text,(g \ "@objectid").toString())
        }
        print(data.head.id)
      }
    }*/
    
    def attributeValueEquals(value: String)(node: Node) = {
     node.attributes.exists(_.value.text == value)
    }
    
    val params = Map("stats" -> "1")   
    val request = RequestBuilding.Get(Uri("http://www.boardgamegeek.com/xmlapi/boardgame/"+31260).withQuery(Query(params)))
    val responseF = http.singleRequest(request)
    val gameDataF = responseF.flatMap { 
      response => Unmarshal(response.entity).to[NodeSeq].map {
        case xml => //println(xml)
        val nameElem = xml \ "boardgame" \ "name" filter attributeValueEquals("true")
        val scoreElem = xml \ "boardgame" \ "statistics" \ "ratings" \ "average"
          GameData(nameElem.text,scoreElem.text)
      }
    }
    gameDataF.onComplete { x => print(x) }
    


    
   // val  x = respF.flatMap{ x => Unmarshal(x.entity).to[NodeSeq] }
    //x.onComplete {  y => print(y.get.\\ ("@objectid")) }
    /*respF.onComplete { 
      case Success(res) => println(
        Future.successful(Await.result(res.entity.toStrict(3000.millis).map{ entity =>
                                                            val xml = Unmarshal(entity).to[NodeSeq]
                                                            val xml2 = Await.result(xml, Duration.Inf)
                                                            print(xml2 \ "boardgame" \ "@objectid")
                                                            },Duration.Inf))  
      )
      case Failure(e) => println("uhoh")
    }*/
    println("done")

  }
}