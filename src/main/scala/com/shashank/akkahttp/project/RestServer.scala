package com.shashank.akkahttp.project

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.apache.spark.sql.SparkSession

/**
 * Created by madhu on 8/11/15.
 */


class RestServer(implicit val system:ActorSystem,
	implicit  val materializer:ActorMaterializer,implicit val
	sparkSession:SparkSession) extends RestService{
		def startServer(address:String, port:Int) = {
			Http().bindAndHandle(route,address,port)
		}
	}

	object RestServer {

		def main(args: Array[String]) {

			implicit val actorSystem = ActorSystem("rest-server")
			implicit val materializer = ActorMaterializer()
			implicit val sparkSession:SparkSession = SparkSession.builder().master("local").
				appName("Rest Server context").getOrCreate()
			val server = new RestServer()
			server.startServer("localhost",8080)
			println("running server at localhost 8080")
		}
	}