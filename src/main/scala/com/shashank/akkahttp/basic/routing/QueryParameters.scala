package com.shashank.akkahttp.basic.routing

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, Multipart, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{MalformedQueryParamRejection, MissingQueryParamRejection}
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.testkit.ScalatestRouteTest


/**
  * Created by shashank on 31/10/16.
  */

case class ColorInfo(color:String, backgroundColor:Option[String])

object QueryParameters extends BaseSpec {

  def main(args: Array[String]) {

    val route =
      path("api1") {
        get{
          //Optional parameter
          parameters('color, 'backgroundColor) { (color, backgroundColor) =>
            complete(s"The color is '$color' and the background is '$backgroundColor'")
          }    
        }
      } ~
      path("api2") {
        get{
          //Optional parameter with default value
          parameters('color, 'backgroundColor.?) { (color, backgroundColor) =>
            val backgroundStr = backgroundColor.getOrElse("no-background")
            complete(s"The color is '$color' and the background is '$backgroundStr'")
          }  
        }
      } ~
      path("api3") {
        get{
          //Parameter with required value
          parameters('color, 'action ! "true") { color =>
            complete(s"The color is '$color'.")
          }  
        }
      } ~
      path("api4") {
        get{
          //Deserialized parameter
          parameters('color, 'count.as[Int]) { (color, count) =>
            complete(s"The color is '$color' and you have $count of it.")
          }  
        }
      } ~
      path("api5") {
        get{
          //Repeated parameter
          parameters('color, 'city.*) { (color, cities) =>
            cities.toList match {
              case Nil         => complete(s"The color is '$color' and there are no cities.")
              case city :: Nil => complete(s"The color is '$color' and the city is $city.")
              case multiple    => complete(s"The color is '$color' and the cities are ${multiple.mkString(", ")}.")
            }
          }
        }
      } ~
      path("api6") {
        get{
          //Deserialized parameter into case class
          parameters('color, 'backgroundColor.?).as(ColorInfo) { color =>
            complete(s"The color information abstracted into color info case class is $color")
          }
        }
      }


      
 
      // tests:
    Get("/api1?color=blue&backgroundColor=red") ~> route ~> check {
      responseAs[String] shouldEqual "The color is 'blue' and the background is 'red'"
    }

    Get("/api1?color=blue") ~> route ~> check {
      rejection shouldEqual MissingQueryParamRejection("backgroundColor")
    }

    Get("/api2?color=blue&backgroundColor=red") ~> route ~> check {
      responseAs[String] shouldEqual "The color is 'blue' and the background is 'red'"
    }
    
    Get("/api2?color=blue") ~> route ~> check {
      responseAs[String] shouldEqual "The color is 'blue' and the background is 'no-background'"
    }


    Get("/api3?color=blue&action=true") ~> route ~> check {
      responseAs[String] shouldEqual "The color is 'blue'."
    }

    Get("/api4?color=blue&count=42") ~> route ~> check {
      responseAs[String] shouldEqual "The color is 'blue' and you have 42 of it."
    }
 
    Get("/api4?color=blue&count=blub") ~> route ~> check {
      rejection.isInstanceOf[MalformedQueryParamRejection] shouldEqual true
      val malformedQueryParamRejection = rejection.asInstanceOf[MalformedQueryParamRejection]
      malformedQueryParamRejection.parameterName shouldEqual "count"
      malformedQueryParamRejection.errorMsg shouldEqual "'blub' is not a valid 32-bit signed integer value"
    }

    Get("/api5?color=blue") ~> route ~> check {
      responseAs[String] === "The color is 'blue' and there are no cities."
    }

    Get("/api5?color=blue&city=Chicago&city=Boston") ~> route ~> check {
      responseAs[String] === "The color is 'blue' and the cities are Chicago, Boston."
    }

    Get("/api6?color=blue&backgroundColor=red") ~> route ~> check {
      responseAs[String] shouldEqual "The color information abstracted into color info case class is ColorInfo(blue,Some(red))"
    }

    system.terminate()
  }

}
