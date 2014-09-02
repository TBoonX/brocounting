package com.github.tboonx

import com.mongodb.DBObject
import com.mongodb.casbah.Imports
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

    val parsedInput: JValue = JsonMethods.parse("{\"name\":\"dab\",\"password\":\"123456\", \"session\" : {\"hash\" : \"asdf\"}}", true)

    it("should be compilable to json without sepecifying an session hash"){
      val fixtureWithoutHash = parsedInput.removeField( (value : JField) =>  {
        "session".equals(value._1)
      })
      val user: User = Extraction.extract[User](fixtureWithoutHash)
      assert("dab" === user.name)
      assert("123456" === user.password)
      assert(List() === user.tags)
      assert(user.session.isInstanceOf[Option[Session]])
    }

    it("should be serializable to a mongo object without session and tags"){
      val user : User = new User("dab", "123456", List(), Option[Session](null))
      val expected = "{ \"_id\" : \"dab\" , \"password\" : \"123456\" , \"tags\" : [ ]}"
      assert(expected === grater[User].asDBObject(user).toString)
    }

    it("should be compilable to json using scalatra-json") {
      val user: User = Extraction.extract[User](parsedInput)
      assert(user.name === "dab")
      assert(user.password === "123456")
      assert(user.tags === List())
      assert(user.session.get === new Session("asdf"))
    }

    it("should be serializable to a mongo db object using salat"){
      val user : User = new User("dan", "123456", List(new Tag("eat", Array(), true), new Tag("sleep", Array(), false), new Tag("code", Array(), false)), Option(new Session("myRandomlyGeneratedSessionHash")))
      val userDbObject: Imports.DBObject = grater[User].asDBObject(user)
      val expected = "{ \"_id\" : \"dan\" , \"password\" : \"123456\" , \"tags\" : [ { \"_id\" : \"eat\" , \"icon\" : <Binary Data> , \"enabled\" : true} , { \"_id\" : \"sleep\" , \"icon\" : <Binary Data> , \"enabled\" : false} , { \"_id\" : \"code\" , \"icon\" : <Binary Data> , \"enabled\" : false}] , \"session\" : { \"_id\" : \"myRandomlyGeneratedSessionHash\"}}"
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

  describe("An transaction instance"){

    DateTimeZone.setDefault(DateTimeZone.UTC)

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
      val transaction: Transaction = new Transaction("dhaeb", "bic", BigDecimal.valueOf(3.14), List(), Option[String](null), new DateTime(1409585982218L))
      val expected = "{ \"agent\" : \"dhaeb\" , \"account\" : \"bic\" , \"amount\" : 3.14 , \"tagNames\" : [ ] , \"date\" : { \"$date\" : \"2014-09-01T15:39:42.218Z\"}}"
      val result: String = grater[Transaction].asDBObject(transaction).toString
      assert(expected === result)
    }

  }
}