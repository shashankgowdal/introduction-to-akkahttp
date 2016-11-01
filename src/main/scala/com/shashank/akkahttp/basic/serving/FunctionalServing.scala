package com.shashank.akkahttp.basic.serving

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, Materializer}

/**
  * Created by shashank on 30/10/16.
  */
object FunctionalServing {

  def main(args: Array[String]) {

    implicit val sys = ActorSystem("IntroductionToAkkaHttp")
    implicit val mat:Materializer = ActorMaterializer()

    val handler :(HttpRequest => HttpResponse) = {
      case HttpRequest(HttpMethods.GET, Uri.Path("/ping"), _, _, _) =>
        HttpResponse(StatusCodes.OK, entity = "pong!")

      case r =>
        HttpResponse(status = StatusCodes.BadRequest)
    }

    Http().bindAndHandleSync(handler, "localhost", 8080)

  }

}
