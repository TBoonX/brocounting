package com.github.tboonx

import com.mongodb.casbah.Imports
import com.novus.salat._
import com.novus.salat.global._
import com.novus.salat.annotations._
import com.tboonx.github.brocounting.model._
import org.json4s._
import org.json4s.jackson.JsonMethods
import org.scalatest.FunSpec

/**
 * Created by dhaeb on 31.08.14.
 */
class UserSpec extends FunSpec {

  protected implicit val jsonFormats: Formats = DefaultFormats

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
}

