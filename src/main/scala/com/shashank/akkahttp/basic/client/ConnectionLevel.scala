package com.shashank.akkahttp.basic.client

import akka.actor.ActorSystem
import akka.http.javadsl.settings.ClientConnectionSettings
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by shashank on 01/11/16.
  */
object ConnectionLevel {

  def main(args: Array[String]) {
    implicit val sys = ActorSystem("IntroductionToAkkaHttp")
    implicit val mat = ActorMaterializer()

    val connectionFlow = Http().outgoingConnection("localhost", 8090)

    val responseFuture =
      Source.single(HttpRequest(uri = "/welcome"))
        .via(connectionFlow)
        .runWith(Sink.head)

    val response = Await.result(responseFuture, 10 seconds)
    response.entity.dataBytes.map(_.utf8String).runForeach(println)
    sys.terminate()
  }

}
