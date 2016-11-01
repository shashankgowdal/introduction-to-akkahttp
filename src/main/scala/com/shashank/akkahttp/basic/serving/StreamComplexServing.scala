package com.shashank.akkahttp.basic.serving

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Host, `Access-Control-Allow-Origin`}
import akka.stream.scaladsl.Flow
import akka.stream.{ActorMaterializer, Materializer}

/**
  * Created by shashank on 31/10/16.
  */
object StreamComplexServing {

  def main(args: Array[String]) {
    implicit val sys = ActorSystem("IntroductionToAkkaHttp")
    implicit val mat:Materializer = ActorMaterializer()

    val redirectHost = "localhost"
    val redirectPort = 8090

    val requestFlow = Flow.fromFunction[HttpRequest, HttpRequest]( request => {
      request
        .withUri(request.uri.withAuthority(redirectHost, redirectPort))
        .mapHeaders(headers => headers.filterNot(_.lowercaseName() == Host.lowercaseName))
        .addHeader(Host(redirectHost, redirectPort))
    })

    val outgoingConnection = Http().outgoingConnection(redirectHost, redirectPort)

    val responseFlow = Flow.fromFunction[HttpResponse, HttpResponse]( response => {
      response.withHeaders(`Access-Control-Allow-Origin`.*)
    })

    Http().bindAndHandle(requestFlow via outgoingConnection via responseFlow, "localhost", 8080)
  }

}
