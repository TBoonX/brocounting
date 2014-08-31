package com.tboonx.github.brocounting

import org.scalatra.ScalatraServlet
import javax.servlet.ServletContext
import org.scalatra.LifeCycle
import com.mongodb.casbah.MongoClient
import com.tboonx.github.brocounting.model.User

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

class ScalaraRestfulApiDef extends ScalatraServlet with JacksonJsonSupport {
  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.
  protected implicit val jsonFormats: Formats = DefaultFormats
  
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
    val mongoClient = MongoClient("localhost", 27017)
    val db = mongoClient("brocounting_test")
    val firstObject = db("user") findOne()	 	
    firstObject.get
  }

  put("/session") {

    val user = parsedBody.extract[User]

    printf("username: "+user.user_name);

    "session123"
  }
}
 
class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new ScalaraRestfulApiDef, "/service/*")
  }
}