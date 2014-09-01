package com.tboonx.github.brocounting.model

import com.novus.salat.EnumStrategy
import com.novus.salat.annotations._
/**
 * Created by dhaeb on 31.08.14.
 */

object ModelConstants {
  @EnumAs(strategy =  EnumStrategy.BY_ID)
  object AccountKind extends Enumeration {
    type AccountKind = Value
    val PAYPAL, CASH, GIRO_ACOUNT = Value
  }
}

import ModelConstants.AccountKind._
import org.joda.time.DateTime

case class User(@Key("_id") name: String, password: String, tags : List[Tag], session : Option[Session])
case class Transaction(agent : String, account : String, amount : BigDecimal, date : DateTime, tags : List[Tag], note : String)
case class Account(@Key("_id") bic : String, name : String, balance : BigDecimal, kind : AccountKind, miscellaneous : Option[Map[String, String]])
case class Session(@Key("_id") hash: String)
case class Tag(@Key("_id") name : String, icon : Array[Byte], enabled : Boolean)
