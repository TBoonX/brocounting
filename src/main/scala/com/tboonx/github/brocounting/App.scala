package com.tboonx.github.brocounting

import org.scalatra.ScalatraServlet
import javax.servlet.ServletContext
import org.scalatra.LifeCycle
import com.mongodb.casbah.MongoClient

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

class ScalaraTestFoo extends ScalatraServlet with JacksonJsonSupport {
  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.
  protected implicit val jsonFormats: Formats = DefaultFormats
  
  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }
  
  get("/") {
    <html>
	Hello to scalatra
	</html>
  }
  
  case class User(user_name: String, user_password: String)
  put("/session") {
    
    val user = parsedBody.extract[User]
    
    printf("username: "+user.user_name);
    
    "session123"
  }
  
  get("/hello_mongo") {
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("brocounting_test")
    val firstObject = db("user") findOne()	 	
    firstObject.get
  }
}
 
class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new ScalaraTestFoo, "/service/*")
  }
}