package com.shashank.akkahttp.basic.routing

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._


/**
  * Created by shashank on 31/10/16.
  */
object FileUpload extends BaseSpec {

  def main(args: Array[String]) {

    val route =
      path("upload"){
        uploadedFile("csv") {
          case (metadata, file) =>
            println(scala.io.Source.fromFile(file).getLines.mkString(","))
            complete(StatusCodes.OK)
        }
      }

    //Test file upload  
    val multipartForm =
      Multipart.FormData(
        Multipart.FormData.BodyPart.Strict(
          "csv",
          HttpEntity(ContentTypes.`text/plain(UTF-8)`, "1,5,7\n11,13,17"),
          Map("filename" -> "data.csv")))

    Post("/upload", multipartForm) ~> route ~> check {
      status shouldEqual StatusCodes.OK
    }

    system.terminate()
  }

}
//File upload direct
//curl --form "csv=@uploadFile.txt" http://<host>:<port>