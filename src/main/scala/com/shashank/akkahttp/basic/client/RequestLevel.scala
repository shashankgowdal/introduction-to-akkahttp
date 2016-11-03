package com.shashank.akkahttp.basic.client

import akka.actor.{ActorSystem, Terminated}
import akka.http.javadsl.settings.ConnectionPoolSettings

import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl._

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by shashank on 01/11/16.
  */
object RequestLevel {

  def main(args: Array[String]) {
    implicit val sys = ActorSystem("IntroductionToAkkaHttp")
    implicit val mat = ActorMaterializer()

    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(uri = "http://localhost:8090/welcome"))

    val response = Await.result(responseFuture, 1 seconds)
    val responseString = Await.result(response.entity.dataBytes.map(_.utf8String).runFold("")(_ + _), 1 second)
    println(responseString)


    Http().shutdownAllConnectionPools().onComplete{ _ =>
      sys.terminate()
    }

  }

}
