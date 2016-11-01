package com.shashank.akkahttp.basic.routing

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.util.ByteString
import com.shashank.akkahttp.basic.routing.CustomEntityWithJsonModels.{Employee, EmployeeList}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._


/**
  * Created by shashank on 31/10/16.
  */

object CustomEntityWithJsonModels {
  case class Employee(id:String, name:String, age:Int, department:Option[String])
  case class EmployeeList(employees:Array[Employee])

  object ServiceJsonProtocol extends DefaultJsonProtocol {
    implicit val employeeFormat = jsonFormat4(Employee)
    implicit val employeeListFormat = jsonFormat1(EmployeeList)
  }
}

object CustomEntityWithJson extends BaseSpec {

  def main(args: Array[String]) {

    import com.shashank.akkahttp.basic.routing.CustomEntityWithJsonModels.ServiceJsonProtocol._
    val employeeBuffer = scala.collection.mutable.ArrayBuffer.empty[Employee]

    val route =
      path("employee"){
        post{
          entity(as[Employee]){ employee => 
            complete {
              if(employeeBuffer.exists(_.id == employee.id))
                require(false, s"${employee.id} already exists")
              employeeBuffer += employee
              employee
            }
          }
        } ~
        get{
          complete {
            EmployeeList(employeeBuffer.toArray)
          }
        }
      }


    Post("/employee", HttpEntity(MediaTypes.`application/json`, ByteString("""{"id":"1", "name":"Eric", "age":30}"""))) ~> route ~> check {
      status shouldEqual StatusCodes.OK
    }

    Get("/employee") ~> route ~> check {
      status shouldEqual StatusCodes.OK
      entityAs[String].parseJson.compactPrint shouldEqual """{"employees":[{"id":"1","name":"Eric","age":30}]}""".parseJson.compactPrint
    }

    system.terminate()
  }

}