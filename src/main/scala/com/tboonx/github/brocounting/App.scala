package com.tboonx.github.brocounting

import java.util.NoSuchElementException
import javax.servlet.ServletContext

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{Imports, MongoClient, MongoCollection, MongoDB}
import com.novus.salat._
import com.novus.salat.global._
import com.tboonx.github.brocounting.model._
import org.scalatra.{CorsSupport, LifeCycle, ScalatraServlet}

// JSON-related libraries

// JSON handling support from Scalatra
import org.scalatra.json._

class ScalaraRestfulApiDef extends ScalatraServlet with JacksonJsonSupport with CorsSupport {
  implicit val jsonFormats = model.jsonFormats
  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.
  private val mongoHost = Option(System.getProperty("mongohost")) getOrElse "localhost"
  private val mongoPort : Int = Option(System.getProperty("mongoport")) getOrElse("27017") toInt
  private val mongoDbName = Option(System.getProperty("mongodb")) getOrElse "brocounting"
  protected val db: MongoDB = MongoClient(mongoHost, mongoPort).apply(mongoDbName)
  
  options("/*") {  put("/session") {
    val user = parsedBody.extract[User]
    db("user").insert(grater[User].asDBObject(user))
    println("finshed inserting testuser: "+user.name)
  }
	  response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
	  response.setHeader("Access-Control-Allow-Origin", "*")
	  response.setHeader("Access-Control-Allow-Methods", request.getHeader("Access-Control-Request-Method"))
  }

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }
  
  get("/") {
    <html>
      <h1 class="myclass">I can do with classes too!</h1>
	    Hello to scalatra
	</html>
  }

  get("/hello_mongo") {
    val firstObject = db("user") findOne()
    firstObject.get
  }

  get("/hello_mongo") {
    val firstObject = db("user") findOne()
    firstObject.get
  }

  post("/session") {
    val user = parsedBody.extract[User]
    db("user").insert(grater[User].asDBObject(user))
    println("finshed inserting testuser: "+user.name)

    new Session("session123")
  }

  get("/session") {
    params("hash") match {
      case "session123" => new Response(true)
      case _ => new Response(false)
    }
  }
}

class UserService extends ScalaraRestfulApiDef {

  post("/create"){
    val addableUser: User = parsedBody.extract[User]
    verifyPassword(addableUser.password)
    val addableUserAsDbObject: Imports.DBObject = grater[User].asDBObject(addableUser)
    db("user").insert(addableUserAsDbObject)
  }

  put("/update"){
    val updatableUser: User = parsedBody.extract[User]
    verifyPassword(updatableUser.password)
    val updatableUserAsDbObject: Imports.DBObject = grater[User].asDBObject(updatableUser)
    db("user").update(MongoDBObject("_id" -> updatableUser.name), updatableUserAsDbObject, false)
  }

  def verifyPassword(pw : String): Unit ={
    if(pw.size < 8){
      response.setStatus(400)
      throw new IllegalArgumentException("password must be at least 8 characters long")
    }
  }

  delete("/delete") {
    val deletableUser: User = parsedBody.extract[User]
    val users = db("user")
    val deletableUserAsMongoObject: Imports.DBObject = grater[User].asDBObject(deletableUser)
    val deletableInDB: MongoCollection#T = users.findOne(MongoDBObject("_id" -> deletableUser.name)).get
    if(deletableUser.password == deletableInDB.get("password")){
      users.remove(deletableUserAsMongoObject)
    } else {
      response.setStatus(403)
      new IllegalArgumentException("The password was not correct!")
    }
  }
}

class AccountService extends ScalaraRestfulApiDef {

  get("/") {
    params("hash") match {
      case "session123" => {

        try {
          val account = params("account")
          //get this account
        }
        catch {
          //get all accounts
          case e: NoSuchElementException => List(new Account("giro123", "Kurt", 1200, AccountKind.GIRO_ACOUNT, Map[String, String]())) ++ List(new Account("paypal1", "Kurt", 23, AccountKind.PAYPAL, Map[String, String]())) ++List(new Account("portmonai", "Kurt", 55, AccountKind.CASH, Map[String, String]()))
        }
      }
      case _ => Some
    }
  }

}

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext): Unit = {
    val servicePrefix = "/service"
    context.mount(new ScalaraRestfulApiDef, "%s/*".format(servicePrefix))
    context.mount(new UserService, "%s/user/*".format(servicePrefix))
    context.mount(new AccountService, "%s/account/*".format(servicePrefix))
  }
}