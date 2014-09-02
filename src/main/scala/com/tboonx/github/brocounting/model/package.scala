package com.tboonx.github.brocounting

import com.novus.salat.EnumStrategy
import com.novus.salat.annotations._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.json4s.JsonAST.{JString, JField, JObject}
import org.json4s.{DefaultFormats, Formats, ShortTypeHints}

@EnumAs(strategy = EnumStrategy.BY_VALUE)
object AccountKind extends Enumeration {
  val PAYPAL, CASH, GIRO_ACOUNT = Value
}

/**
 * Created by dhaeb on 02.09.14.
 */
package object model {
  val dateTimeHint = new ShortTypeHints (classOf[DateTime] :: Nil) {
    private val fieldName: String = "$date"
    private val isoTimeFormatter = ISODateTimeFormat.dateTime()

    override def serialize: PartialFunction[Any, JObject] = {
      case t: DateTime => JObject (JField (fieldName, new JString(isoTimeFormatter.print(t)) ) :: Nil)
    }

    override def deserialize: PartialFunction[(String, JObject), Any] = {
      case ("DateTime", JObject (JField (fieldName, JString (content) ) :: Nil) ) => {
        isoTimeFormatter.parseDateTime(content)
      }
    }
  }

  final implicit val jsonFormats: Formats = DefaultFormats.withHints(dateTimeHint)
}
