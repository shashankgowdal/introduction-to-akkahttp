package com.shashank.akkahttp.basic.serving

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Flow
import akka.stream.{ActorMaterializer, Materializer}

/**
  * Created by shashank on 30/10/16.
  */
object StreamsServing {

  def main(args: Array[String]) {

    implicit val sys = ActorSystem("IntroductionToStream")
    implicit val mat:Materializer = ActorMaterializer()

    val requestResponseFlow = Flow.fromFunction[HttpRequest, HttpResponse]( request => {
      println(request.toString)
      HttpResponse(StatusCodes.OK, entity = "Hello!")
    })

    Http().bindAndHandle(requestResponseFlow, "localhost", 8080)

  }

}
