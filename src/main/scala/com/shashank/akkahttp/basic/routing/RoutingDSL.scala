package com.shashank.akkahttp.basic.routing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.{ActorMaterializer, Materializer}

/**
  * Created by shashank on 31/10/16.
  */
object RoutingDSL {

  def main(args: Array[String]) {

    implicit val sys = ActorSystem("IntroductionToAkkaHttp")
    implicit val mat:Materializer = ActorMaterializer()

    val route =
      path("welcome"){
        get{
          complete {
            "welcome to rest service"
          }
        }
      } ~
      path("demo"){
        get{
          complete {
            "welcome to demonstration"
          }
        }
      }

    Http().bindAndHandle(route, "localhost", 8090)

  }

}
