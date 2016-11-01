package com.shashank.akkahttp.basic.routing

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.util.ByteString
import com.shashank.akkahttp.basic.routing.CustomDirectiveModels.{Employee, EmployeeList}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.headers.{Authorization, RawHeader}
import akka.http.scaladsl.server._
import com.shashank.akkahttp.util.JWTUtils
import com.shashank.akkahttp.util.JWTUtils.User
import spray.json._

import scala.collection.immutable


/**
  * Created by shashank on 31/10/16.
  */

object CustomDirectiveModels {
  case class Employee(id:String, name:String, age:Int, department:Option[String])
  case class EmployeeList(employees:Array[Employee])

  object ServiceJsonProtocol extends DefaultJsonProtocol {
    implicit val employeeFormat = jsonFormat4(Employee)
    implicit val employeeListFormat = jsonFormat1(EmployeeList)
  }
}

object CustomDirective extends BaseSpec {

  def main(args: Array[String]) {

    import com.shashank.akkahttp.basic.routing.CustomDirectiveModels.ServiceJsonProtocol._
    val employeeBuffer = scala.collection.mutable.ArrayBuffer.empty[Employee]

    val decodeJWTToUser: Directive1[User] =
    optionalHeaderValueByName("token").map[User]({
          case Some(bearer) =>
            JWTUtils.decodeJWTToUser(bearer).getOrElse(User("invalid-token", false))
          case None =>
            User("missing-token", false)
        })

    val authenticate: Directive0 = {
      decodeJWTToUser.flatMap(user => {
        user.name match {
          case "invalid-token" => reject(MalformedHeaderRejection("token", "invalid jwt token"))
          case "missing-token" => reject(MissingHeaderRejection("token"))
          case _ => pass
        }
      })
    }


    val authorize:Directive0 = {
      decodeJWTToUser.flatMap(user => {
        if(user.admin) pass
        else extractMethod.flatMap({
          case HttpMethods.POST => reject(AuthorizationFailedRejection)
          case _ => pass
        })
      })
    }


    val postOrPut = put | post



    val route =
      authenticate {
        path("employee") {
          postOrPut {
            authorize{
              entity(as[Employee]) { employee =>
                complete {
                  if (employeeBuffer.exists(_.id == employee.id))
                    require(false, s"${employee.id} already exists")
                  employeeBuffer += employee
                  employee
                }
              }
            }
          } ~
          get {
            complete {
              EmployeeList(employeeBuffer.toArray)
            }
          }
        }
      }


    HttpRequest(
      HttpMethods.POST,
      "/employee",
      immutable.Seq(RawHeader("token", JWTUtils.adminToken)),
      HttpEntity(MediaTypes.`application/json`, ByteString("""{"id":"1", "name":"Eric", "age":30}"""))) ~> route ~> check {
      status shouldEqual StatusCodes.OK
    }

    HttpRequest(
      HttpMethods.POST,
      "/employee",
      immutable.Seq(RawHeader("token",JWTUtils.myToken)),
      HttpEntity(MediaTypes.`application/json`, ByteString("""{"id":"1", "name":"Eric", "age":30}"""))) ~> route ~> check {
      rejection shouldEqual AuthorizationFailedRejection
    }

    HttpRequest(
      HttpMethods.POST,
      "/employee",
      immutable.Seq(RawHeader("token","some_senseless_token")),
      HttpEntity(MediaTypes.`application/json`, ByteString("""{"id":"1", "name":"Eric", "age":30}"""))) ~> route ~> check {
      rejection shouldEqual MalformedHeaderRejection("token", "invalid jwt token")
    }

    HttpRequest(
      HttpMethods.GET,
      "/employee") ~> route ~> check {
      rejection shouldEqual MissingHeaderRejection("token")
    }

    HttpRequest(
      HttpMethods.GET,
      "/employee",
      immutable.Seq(RawHeader("token","eyJhbGciOiJIUzUxMiJ9.eyJuYW1lIjoiU2hhc2hhbmsiLCJhZG1pbiI6ZmFsc2V9.smlXLOZFZ14fozEwULbiSvzDEStlVjnLWSmg6MiaDDXUirCJjPpkNrzpKI31MxID0ZUV-H3tEcPmB9jJjGl9qA"))) ~> route ~> check {
      status shouldEqual StatusCodes.OK
      entityAs[String].parseJson.compactPrint shouldEqual """{"employees":[{"id":"1","name":"Eric","age":30}]}""".parseJson.compactPrint
    }

    system.terminate()
  }

}