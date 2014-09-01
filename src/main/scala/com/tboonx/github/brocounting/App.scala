package com.tboonx.github.brocounting

import com.mongodb.casbah.commons.{MongoDBObjectBuilder, MongoDBObject}
import org.scalatra.ScalatraServlet
import javax.servlet.ServletContext
import org.scalatra.LifeCycle
import org.scalatra.CorsSupport
import com.mongodb.casbah.{Imports, MongoCollection, MongoDB, MongoClient}
import com.tboonx.github.brocounting.model._
import com.novus.salat._
import com.novus.salat.global._

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

class ScalaraRestfulApiDef extends ScalatraServlet with JacksonJsonSupport with CorsSupport {
  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.
  protected implicit val jsonFormats: Formats = DefaultFormats
  val mongoHost = Option(System.getProperty("mongohost")) getOrElse "localhost"
  val mongoPort : Int = Option(System.getProperty("mongoport")) getOrElse("27017") toInt
  val mongoDbName = Option(System.getProperty("mongodb")) getOrElse "brocounting"
  private val db: MongoDB = MongoClient(mongoHost, mongoPort).apply(mongoDbName)
  
  options("/*") {
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

  put("/user/upsert"){
    println(parsedBody)
    val addableUser: User = parsedBody.extract[User]
    val addableUserAsDbObject: Imports.DBObject = grater[User].asDBObject(addableUser)
    db("user").update(MongoDBObject("_id" -> addableUser.name), addableUserAsDbObject, true)
  }

  delete("/user/delete") {
    val deletableUser: User = parsedBody.extract[User]
    val users = db("user")
    val deletableUserAsMongoObject: Imports.DBObject = grater[User].asDBObject(deletableUser)
    val deletableInDB: MongoCollection#T = users.findOne(deletableUserAsMongoObject).get
    if(deletableUser.password == deletableInDB.get("password")){
      users.remove(deletableUserAsMongoObject)
    }
  }

  get("/hello_mongo") {
    val firstObject = db("user") findOne()
    firstObject.get
  }

  put("/session") {
    val user = parsedBody.extract[User]
    db("user").insert(grater[User].asDBObject(user))
    println("finshed inserting testuser: "+user.name)
  }
}
 
class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new ScalaraRestfulApiDef, "/service/*")
  }
}