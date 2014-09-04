package com.github.tboonx

import com.mongodb.DBObject
import com.mongodb.casbah.{commons, Imports}
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.{MongoDBObject, MongoDBListBuilder}
import com.novus.salat._
import com.novus.salat.global._
import com.tboonx.github.brocounting.model._
import com.tboonx.github.brocounting.{AccountKind, model}
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s._
import org.json4s.jackson.JsonMethods
import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TestModel extends FunSpec {

  DateTimeZone.setDefault(DateTimeZone.UTC)
  val fixtureTime: DateTime = new DateTime(1409585982218L)

  describe("The typehint dateTimeHint"){
    val testable = model.dateTimeHint

    it("should be able to serialize an datatime object"){
      val serilizable: DateTime = DateTime.now()
      val serialized: JsonAST.JObject = testable.serialize(serilizable)
      assert("JObject(List(($date,JString(%s))))".format(serilizable.toDateTimeISO()) === serialized.toString)
    }

    it("should be able to deserialize an datatime object from an iso 8601 json encoded object"){
      val fixture: JValue = JsonMethods.parse(
        """
           {"$date" : "2014-09-01T15:39:42.218Z"}
        """.stripMargin)
      assert(new DateTime(1409585982218L) === testable.deserialize("DateTime", fixture.asInstanceOf[JObject]) )
    }

    it("should be possible to apply serialize and deserialize in order and to get the origin input value"){
      val serilizable: DateTime = DateTime.now()
      assert(serilizable == testable.deserialize( ("DateTime", testable.serialize(serilizable)) ))
    }
  }

  describe("A user instance") {

    val parsedInput: JValue = JsonMethods.parse("{\"name\":\"dab\",\"password\":\"12345678\" }", true)

    it("should be compilable to json without sepecifying an session hash"){
      val fixtureWithoutHash = parsedInput.removeField( (value : JField) =>  {
        "session".equals(value._1)
      })
      val user: User = Extraction.extract[User](fixtureWithoutHash)
      assert("dab" === user.name)
      assert("12345678" === user.password)
      assert(List() === user.tags)
    }

    it("should be serializable to a mongo object without session and tags"){
      val user : User = new User("dab", "12345678", List())
      val expected = "{ \"_id\" : \"dab\" , \"password\" : \"12345678\" , \"tags\" : [ ]}"
      assert(expected === grater[User].asDBObject(user).toString)
    }

    it("should be compilable to json using scalatra-json") {
      val user: User = Extraction.extract[User](parsedInput)
      assert(user.name === "dab")
      assert(user.password === "12345678")
      assert(user.tags === List())
    }

    it("should be serializable to a mongo db object using salat"){
      val user : User = new User("dan", "12345678", List(new Tag("eat", Array(), true), new Tag("sleep", Array(), false), new Tag("code", Array(), false)))
      val userDbObject: Imports.DBObject = grater[User].asDBObject(user)
      val expected = "{ \"_id\" : \"dan\" , \"password\" : \"12345678\" , \"tags\" : [ { \"_id\" : \"eat\" , \"icon\" : <Binary Data> , \"enabled\" : true} , { \"_id\" : \"sleep\" , \"icon\" : <Binary Data> , \"enabled\" : false} , { \"_id\" : \"code\" , \"icon\" : <Binary Data> , \"enabled\" : false}]}"
      assert(expected === userDbObject.toString)
    }

  }

  describe("An account instance"){
    it("should be compilable to an scala object"){
      val parsableInput = "{\"_id\" : \"myBic\", \"balance\" : 3000.25, \"kind\" : \"CASH\", \"owner\" : \"Dan Häberlein\", \"miscellaneous\" : {\"key\" : \"value\"} }"
      println(grater[Account].fromJSON(parsableInput))
    }

    it("should be convertable to a MongoDBObject using salat"){
      val inputBigDec: BigDecimal = BigDecimal.valueOf(3.23d)
      val miscesslaneous: Map[String, String] = Map(("a" -> "b"))
      val fixtureAccount: Account = new Account("bic", "name", inputBigDec, AccountKind.CASH, miscesslaneous)
      val result: Imports.DBObject = grater[Account].asDBObject(fixtureAccount)
      assert("bic" === result.get("_id"))
      assert("name" === result.get("owner"))
      assert(inputBigDec === BigDecimal.valueOf(result.get("balance").asInstanceOf[Double]))
      assert(AccountKind.CASH.toString === result.get("kind"))
      assert(miscesslaneous.get("a").get === result.get("miscellaneous").asInstanceOf[DBObject].get("a"))
    }
  }

  describe("An session instance") {
    it("should be creatable from a json string using json4s"){
      val parsableInput = JsonMethods.parse("{\"hash\" : \"asdf\", \"user\" : \"dan\",  \"acquiredAt\" : {\"jsonClass\" : \"DateTime\", \"$date\" : \"2014-09-01T15:39:42.218Z\"} }")
      val session: Session = Extraction.extract[Session](parsableInput)
      assert("asdf" === session.hash)
      assert("dan" === session.user)
      assert(fixtureTime === session.acquiredAt)
    }

    it("should be serializable to mongo db object using salat"){
      val fixtureSession = Session("username")
      val sessionAsDbObject: Imports.DBObject = grater[Session].asDBObject(fixtureSession)
      assert(fixtureSession.hash === sessionAsDbObject.get("_id"))
      assert("username" === sessionAsDbObject.get("user"))
      assert(fixtureSession.acquiredAt === sessionAsDbObject.get("acquiredAt"))
    }

    it("should be createable from a MongoDbObject") {
      val idValue: String = "myhashValue"
      val user: String = "theReferncedUserName"
      val db: commons.Imports.DBObject = (MongoDBObject.newBuilder += ("_id" -> idValue)
                                                                   += ("user" -> user)
                                                                   += ("acquiredAt" -> fixtureTime)).result()
      val comparable: Session = grater[Session].asObject(db)
      assert(idValue === comparable.hash)
      assert(user === comparable.user)
      assert(fixtureTime === comparable.acquiredAt)
    }
  }

  describe("An transaction instance"){

    it("should check the output for lift json mapping for a transaction"){
      val t: Transaction = new Transaction("dhaeb", "bic", BigDecimal.valueOf(4000.00), List(), Option[String](null), DateTime.now())
      val input: String = grater[Transaction].toPrettyJSON(t)
      println(input)
      println(grater[Transaction].fromJSON(input))
    }

    it("should be compilable to JSON from a string"){
      val parsableInput = "{\"agent\" : \"dhaeb\", \"account\" : \"bic\", \"amount\" : 3.14, \"date\" : {\"jsonClass\":\"DateTime\", \"$date\" : \"2014-09-01T00:00:00.000Z\"}, \"note\" : \"my important note\", \"tagNames\" : [\"food\", \"love\"]}"
      val jsonInput: JValue = JsonMethods.parse(parsableInput)
      val transaction : Transaction = Extraction.extract[Transaction](jsonInput)
      assert("dhaeb" === transaction.agent)
      assert("bic" === transaction.account)
      assert(BigDecimal.valueOf(3.14) === transaction.amount)
      assert(new DateTime(2014, 9, 1, 0, 0, 0) === transaction.date)
      assert("my important note" === transaction.note.get)
      assert(List("food", "love") === transaction.tagNames)
    }

    it("should be convertable to an mongo db object using salat"){
      val transaction: Transaction = new Transaction("dhaeb", "bic", BigDecimal.valueOf(3.14), List(), Option[String](null), fixtureTime)
      val expected = "{ \"agent\" : \"dhaeb\" , \"account\" : \"bic\" , \"amount\" : 3.14 , \"tagNames\" : [ ] , \"date\" : { \"$date\" : \"2014-09-01T15:39:42.218Z\"}}"
      val resultDbObject: Imports.DBObject = grater[Transaction].asDBObject(transaction)
      assert(expected === resultDbObject.toString)
    }

  }
}
