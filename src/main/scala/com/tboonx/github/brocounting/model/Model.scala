package com.tboonx.github.brocounting.model

import com.novus.salat.annotations._
import com.tboonx.github.brocounting.AccountKind
import org.joda.time.DateTime

import scala.util.Random

/**
 * Created by dhaeb on 31.08.14.
 */

case class User(@Key("_id") name: String, password: String, tags: List[Tag]){
  def verifyPassword(): Unit ={
    if(password.size < 8){
      throw new IllegalArgumentException("password must be at least 8 characters long")
    }
  }
  verifyPassword()
}

case class Account(@Key("_id") bic: String, owner: String, balance: BigDecimal, kind: AccountKind.Value, miscellaneous: Map[String, String])

case class Transaction(agent: String, account: String, amount: BigDecimal, tagNames: List[String], note: Option[String], date: DateTime)

object Session {
  val randomGenerator : Random = new Random()
  
  def createRandomByteArray() : Array[Byte] = {
    val generator: Random = randomGenerator
    val returnable: Array[Byte] = Array[Byte](16)
    generator.nextBytes(returnable)
    returnable
  }

  def apply(user : String) = new Session(MD5.hash(Session.createRandomByteArray()), user, DateTime.now)
}

case class Session(@Key("_id") hash: String, user : String, var acquiredAt : DateTime)

case class Tag(@Key("_id") name: String, icon: Array[Byte], enabled: Boolean)

case class Response(response: Boolean)