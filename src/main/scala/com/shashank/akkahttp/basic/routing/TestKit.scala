package com.shashank.akkahttp.basic.routing

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import org.scalatest.{ Matchers, WordSpec }
import akka.http.scaladsl.testkit.ScalatestRouteTest


/**
  * Created by shashank on 31/10/16.
  */
object TestKit extends WordSpec with Matchers with ScalatestRouteTest {

  def main(args: Array[String]) {

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


    val getRequest = HttpRequest(GET, "/welcome")

    getRequest ~> route ~> check {
      status.intValue shouldEqual 200
      entityAs[String] shouldEqual "welcome to rest service"
    }

    system.terminate()
  }

}
