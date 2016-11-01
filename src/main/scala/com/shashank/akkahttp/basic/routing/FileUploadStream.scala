package com.shashank.akkahttp.basic.routing

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, Multipart, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Framing
import akka.util.ByteString

import scala.concurrent.Future


/**
  * Created by shashank on 31/10/16.
  */
object FileUploadStream extends BaseSpec{

  def main(args: Array[String]) {

    val route =
      extractRequestContext { ctx =>
        implicit val materializer = ctx.materializer
        implicit val ec = ctx.executionContext
 
        fileUpload("csv") {
          case (metadata, byteSource) =>
            val sumF: Future[Int] =
            // sum the numbers as they arrive so that we can
            byteSource.via(Framing.delimiter(ByteString("\n"), 1024))
              .mapConcat(_.utf8String.split(",").toVector)
              .map(_.toInt)
              .runFold(0) { (acc, n) => acc + n }
              onSuccess(sumF) { sum => complete(s"Sum: $sum") }
        }
      }

    //Test file upload stream
    val multipartForm =
      Multipart.FormData(Multipart.FormData.BodyPart.Strict(
        "csv",
        HttpEntity(ContentTypes.`text/plain(UTF-8)`, "2,3,5\n7,11,13,17,23\n29,31,37\n"),
        Map("filename" -> "primes.csv")))
 
    Post("/", multipartForm) ~> route ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[String] shouldEqual "Sum: 178"
    }

    system.terminate()
  }

}
//File upload direct
//curl --form "csv=@uploadFile.txt" http://<host>:<port>