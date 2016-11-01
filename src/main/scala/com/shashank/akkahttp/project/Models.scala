package com.shashank.akkahttp.project

import spray.json.DefaultJsonProtocol

/**
  * Created by madhu on 8/11/15.
  */
object Models {
  case class LoadRequest(path:String)

  object ServiceJsonProtoocol extends DefaultJsonProtocol {
    implicit val loadRequestFormat = jsonFormat1(LoadRequest)
  }
}