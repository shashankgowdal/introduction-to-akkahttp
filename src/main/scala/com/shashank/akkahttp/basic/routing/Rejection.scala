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
object Rejection {

  def main(args: Array[String]) {

    implicit val sys = ActorSystem("IntroductionToAkkaHttp")
    implicit val mat:Materializer = ActorMaterializer()

    implicit def myRejectionHandler = RejectionHandler.newBuilder().handle{
      case MissingCookieRejection(cookieName) =>
        complete(HttpResponse(StatusCodes.BadRequest, entity = "No cookies, no service!!!"))
    }.handleNotFound {
      complete((StatusCodes.NotFound, "Not here!"))
    }.result()

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
      } ~
      path("wrong"){
        reject{
          ValidationRejection("Invalid path", None)
        }
      }

    Http().bindAndHandle(route, "localhost", 8090)

  }

}
