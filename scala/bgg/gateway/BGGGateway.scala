package bgg.gateway

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.Uri.Path
import akka.stream.scaladsl.{ Source, Sink }
import akka.stream.scaladsl.Flow




class BGGGateway {
  
  
 // lazy val bggConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
//  Http().outgoingConnection("localhost", 9001)
    
//  def bggRequest(request:HttpRequest): Future[HttpResponse] = 
 // Source.single(request).via(bggConnectionFlow).runWith(Sink.head)
  
}