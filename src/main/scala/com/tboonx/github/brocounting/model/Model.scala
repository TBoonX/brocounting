package com.tboonx.github.brocounting.model

import com.novus.salat.annotations._
import com.tboonx.github.brocounting.AccountKind
import org.joda.time.DateTime

/**
 * Created by dhaeb on 31.08.14.
 */

case class User(@Key("_id") name: String, password: String, tags: List[Tag], session: Option[Session])

case class Account(@Key("_id") bic: String, owner: String, balance: BigDecimal, kind: AccountKind.Value, miscellaneous: Map[String, String])

case class Transaction(agent: String, account: String, amount: BigDecimal, tagNames: List[String], note: Option[String], date: DateTime)

object Transaction {
  com.mongodb.casbah.commons.conversions.scala.RegisterJodaTimeConversionHelpers()
}

case class Session(@Key("_id") hash: String)

case class Tag(@Key("_id") name: String, icon: Array[Byte], enabled: Boolean)
