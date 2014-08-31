package com.tboonx.github.brocounting

import org.scalatra.ScalatraServlet
import javax.servlet.ServletContext
import org.scalatra.LifeCycle
import org.scalatra.CorsSupport
import com.mongodb.casbah.MongoClient
import com.tboonx.github.brocounting.model._
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

/**
 * @author ${user.name}
 */
object App {
  
  def foo(x : Array[String]) = x.foldLeft("")((a,b) => a + b)
  
  def main(args : Array[String]) {
    println( "Hello World!" )
    println("concat arguments = " + foo(args))
  }

}

class ScalaraRestfulApiDef extends ScalatraServlet with JacksonJsonSupport with CorsSupport {
  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.
  protected implicit val jsonFormats: Formats = DefaultFormats
  val mongoClient = MongoClient("localhost", 27017)
  
  options("/*") {
	  response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
	  response.setHeader("Access-Control-Allow-Origin", "*");
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
    val db = mongoClient("brocounting_test")
    val firstObject = db("user") findOne()	 	
    firstObject.get
  }

  put("/session") {
    val user = parsedBody.extract[User]
    val db = mongoClient("brocounting_test")
    db("user").insert(grater[User].asDBObject(user))
    println("finshed inserting testuser: "+user.user_name);
    
    Session("session123")
  }
}
 
class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new ScalaraRestfulApiDef, "/service/*")
  }
}