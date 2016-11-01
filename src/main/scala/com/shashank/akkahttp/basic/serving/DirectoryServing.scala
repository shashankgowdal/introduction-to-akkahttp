package com.shashank.akkahttp.basic.serving

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RejectionHandler, Route}
import akka.stream.{ActorMaterializer, Materializer}


/**
  * Created by shashank on 29/10/16.
  */
object DirectoryServing {

  def main(args: Array[String]) {

    implicit val sys = ActorSystem("IntroductionToAkkaHttp")
    implicit val mat:Materializer = ActorMaterializer()

    val route =
    pathPrefix("demo") {
      getFromBrowseableDirectory("/Users/shashank/Desktop")
    }

    Http(sys).bindAndHandle(route, "localhost", 8090)

  }

}
