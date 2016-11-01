package com.shashank.akkahttp.basic.routing

import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage, UpgradeToWebSocket}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.WSProbe
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.util.ByteString
import scala.concurrent.duration._

/**
  * Created by shashank on 01/11/16.
  */
object Websocket extends BaseSpec{

  def main(args: Array[String]) {

    val greeterWebSocketService: Flow[Message, Message, Any] =
      Flow[Message]
        .mapConcat {
          case tm: TextMessage ⇒ TextMessage(Source.single("Hello ") ++ tm.textStream) :: Nil
          case bm: BinaryMessage =>
            bm.dataStream.to(Sink.ignore)
            Nil
        }



    val route =
      path("subscribe"){
        handleWebSocketMessages(greeterWebSocketService)
      }


    val wsClient = WSProbe()
    WS("/subscribe", wsClient.flow) ~> route ~> check {
      wsClient.sendMessage("Shashank")
      wsClient.expectMessage("Hello Shashank")

      wsClient.sendMessage(ByteString("Some bytes"))
      wsClient.expectNoMessage(100 millis)

      wsClient.sendMessage("Http")
      wsClient.expectMessage("Hello Http")
    }

    system.terminate()

  }

}
