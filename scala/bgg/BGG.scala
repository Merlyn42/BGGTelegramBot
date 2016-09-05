package bgg

import akka.actor.Actor
import akka.actor.Terminated
import data.Score
import data.Request
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.ActorMaterializerSettings
import akka.util.ByteString
import akka.actor.ActorRef
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.Uri.Query
import scala.xml.NodeSeq
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import scala.concurrent.Future
import akka.http.scaladsl.model.StatusCodes.Success

case class SearchResult(name:String,id:String)
case class GameData(name:String,score:String)

class BGG(parent:ActorRef) extends Actor {
  
  import akka.pattern.pipe
  import context.dispatcher
 
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
 
  val http = Http(context.system)
  implicit val timeout = Timeout(5 seconds)

  def receive = {
    case Score(name) =>
      val searchResultF = search(name, true)
      searchResultF onSuccess {
        case result => 
          result match {
            case Left(x) => sender() ! x
            case Right(x) => 
              val data = lookup(x.head.id)
          }       
          
      }
      searchResultF onFailure {
        case t => sender() ! "There was an error contacting BGG"
      }
      //searchGameID(name).flatMap { 
//        id => getGameScore(id) 
  //    }.pipeTo(sender())
    case request:Request =>  sender() ! "No Score found"
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      parent !("Got response, body: " + entity.dataBytes.runFold(ByteString(""))(_ ++ _))
    case x => println("unknown"+x) 
  }
  
  def search(name:String,exact:Boolean):Future[Either[String,Seq[SearchResult]]] = {
    val params = Map("search" -> name, "exact" -> {if (exact) "1" else "0"})   
    val request = RequestBuilding.Get(Uri("http://www.boardgamegeek.com/xmlapi/search").withQuery(Query(params)))
    val responseF = http.singleRequest(request)
    val searchResultF = responseF.flatMap { 
      x => Unmarshal(x.entity).to[NodeSeq].map{
        xml => val games = xml \ "boardgame"
        if(games.length>0){
          Right(games.map { g => SearchResult((xml \\ "name").text,(xml \\ "@objectid").toString())})
        }else{
          Left("No game with that name found")
        }
      }
    }
    return searchResultF
  }
  
  def lookup(id:String):Future[GameData] = {
    val params = Map("stats" -> "1")   
    val request = RequestBuilding.Get(Uri("http://www.boardgamegeek.com/xmlapi/boardgame/"+id).withQuery(Query(params)))
    val responseF = http.singleRequest(request)
    val gameDataF = responseF.flatMap { 
      response => Unmarshal(response.entity).to[NodeSeq].map { xml => GameData((xml \\ "name").text,(xml \\ "average").text) }
    }
    return gameDataF  
  }
  
  def searchGameID(name:String):Future[String] ={
    val params = Map("search" -> name, "exact" -> "1")   
    val request = RequestBuilding.Get(Uri("http://www.boardgamegeek.com/xmlapi/search").withQuery(Query(params)))
    val responseF = http.singleRequest(request)
    val idF = responseF.flatMap { x => Unmarshal(x.entity).to[NodeSeq].map { y => (y \\ "@objectid").toString() } }
    return idF
  }
  
  def getGameScore(id:String):Future[String] = {
    if(id.length()==0){
      return Future.successful("No game found by that name")
    }
    val params = Map("stats" -> "1")   
    val request = RequestBuilding.Get(Uri("http://www.boardgamegeek.com/xmlapi/boardgame/"+id).withQuery(Query(params)))
    val responseF = http.singleRequest(request)
    val scoreF = responseF.flatMap { 
      response => Unmarshal(response.entity).to[NodeSeq].map { 
        rootNode => val avgNode = rootNode \\ "average"
        avgNode.text 
      } 
    }
    return scoreF
  }  
}