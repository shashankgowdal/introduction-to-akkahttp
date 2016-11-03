package com.shashank.akkahttp

import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

import scala.language.postfixOps

/**
  * Created by shashank on 29/10/16.
  */

object IntroductionToStream {

  def main(args: Array[String]) {

    implicit val sys = ActorSystem("IntroductionToStream")
    implicit val mat:Materializer = ActorMaterializer()

    val source = Source(List(1, 2, 3))

    val flow = Flow[Int].map(_.toString)

    val sink = Sink.foreach(println)

    val runnableGraph = source via flow to sink
    runnableGraph.run()
    runnableGraph.run()

    sys.terminate()

  }

}
