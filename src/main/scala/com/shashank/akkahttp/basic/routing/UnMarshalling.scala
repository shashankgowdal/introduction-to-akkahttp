package com.shashank.akkahttp.basic.routing

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, MessageEntity}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import spray.json._

/**
  * Created by shashank on 31/10/16.
  */
object UnMarshalling {

  def main(args: Array[String]) {

    implicit val sys = ActorSystem("IntroductionToAkkaHttp")
    implicit val mat:Materializer = ActorMaterializer()

    //type FromStringUnmarshaller[T] = Unmarshaller[String, T]
    val intFuture = Unmarshal("42").to[Int]
    val int = Await.result(intFuture, 1.second)
    println("int unmarshalling "+int)

    //type FromStringUnmarshaller[T] = Unmarshaller[String, T]
    val boolFuture = Unmarshal("off").to[Boolean]
    val bool = Await.result(boolFuture, 1.second)
    println("off unmarshalling "+bool)

    //type ToEntityMarshaller[T] = Marshaller[T, MessageEntity]
    val string = "Yeah"
    val entityFuture = Marshal(string).to[MessageEntity]
    val entity = Await.result(entityFuture, 1.second) // don't block in non-test code!
    println(entity)

    //type ToResponseMarshaller[T] = Marshaller[T, HttpResponse]
    val errorMsg = "Not found, pal!"
    val responseFuture = Marshal(404 -> errorMsg).to[HttpResponse]
    val response = Await.result(responseFuture, 1.second)
    println(response)


    //type FromEntityUnmarshaller[T] = Unmarshaller[HttpEntity, T]
    val jsonByteString = ByteString("""{"name":"Hello"}""")
    val httpRequest = HttpRequest(HttpMethods.POST, entity = jsonByteString)
    val jsonDataUnmarshalledFuture = Unmarshal(httpRequest).to[String]
    val jsonDataUnmarshalled = Await.result(jsonDataUnmarshalledFuture, 1.second)
    println(jsonDataUnmarshalled)

    sys.terminate()

  }

}
