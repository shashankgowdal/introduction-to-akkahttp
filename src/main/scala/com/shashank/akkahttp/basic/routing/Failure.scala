package com.shashank.akkahttp.basic.routing

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import akka.stream.{ActorMaterializer, Materializer}

/**
  * Created by shashank on 31/10/16.
  */
object Failure {

  def main(args: Array[String]) {

    implicit val sys = ActorSystem("IntroductionToAkkaHttp")
    implicit val mat:Materializer = ActorMaterializer()

    implicit def myExceptionHandler = ExceptionHandler {
      case _: ArithmeticException =>
        complete(HttpResponse(StatusCodes.BadRequest, entity = "Bad numbers, bad result!!!"))
      case e: Throwable => {
        println(e.getMessage)
        println(e.getStackTraceString)
        complete(HttpResponse(StatusCodes.BadRequest, entity = e.getMessage))
      }
    }

    val route =
      path("welcome"){
        get{
          complete {
            "welcome to rest service"
          }
        }
      } ~
      path("demo"){
        get {
          complete {
            100/0
            "welcome to demonstration"
          }
        }
      }

    Http().bindAndHandle(route, "localhost", 8090)
  }

}
