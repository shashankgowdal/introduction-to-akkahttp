package com.shashank.akkahttp.util

import io.jsonwebtoken.Jwts
import java.nio.charset.StandardCharsets
import javax.xml.bind.DatatypeConverter

/**
  * Created by shashank on 01/11/16.
  */
object JWTUtils {
  case class User(name:String, admin:Boolean)

  val adminToken = "eyJhbGciOiJIUzUxMiJ9.eyJuYW1lIjoiYWRtaW4iLCJhZG1pbiI6dHJ1ZX0.c6wRZ4pla6D9f_nDO6tqwyq5KFwyW2iSkKvrwGejn2IMxU_Z273cKZAW3Fu51Cwhp-4vwqOr1aWnyUIwzb_eow"
  val myToken = "eyJhbGciOiJIUzUxMiJ9.eyJuYW1lIjoiU2hhc2hhbmsiLCJhZG1pbiI6ZmFsc2V9.smlXLOZFZ14fozEwULbiSvzDEStlVjnLWSmg6MiaDDXUirCJjPpkNrzpKI31MxID0ZUV-H3tEcPmB9jJjGl9qA"

  private val secretKey = DatatypeConverter.printBase64Binary("introductiontoakkahttp".getBytes(StandardCharsets.UTF_8))


  def decodeJWTToUser(tokenString:String):Option[User] = {
    try {
      val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(tokenString).getBody
      Some(User(claims.get("name").asInstanceOf[String], claims.get("admin").asInstanceOf[Boolean]))
    } catch  {
      case e :  Exception => {
        println("exception in decode token: " + e.getMessage())
        println("Considering it as invalid token")
        None
      }
    }
  }


}
