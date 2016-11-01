package com.shashank.akkahttp.streams

import akka.stream.scaladsl.{Flow, GraphDSL, Sink, Source}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

import scala.language.postfixOps

/**
  * Created by shashank on 31/10/16.
  */
object FastSource {

  def main(args: Array[String]) {

    implicit val sys = ActorSystem("IntroductionToStream")
    implicit val mat:Materializer = ActorMaterializer()

    val source = Source.fromIterator[Int](() => Iterator from 0)

    val flow = Flow[Int].map(x => {
      println(s"Inside thread sleep $x")
      Thread.sleep(1000)
      x
    })

    val sink = Sink.foreach(println)

    val runnableGraph = source via flow to sink
    runnableGraph.run()

  }

}
