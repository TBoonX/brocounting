package com.tboonx.github.brocounting

import org.scalatra.ScalatraServlet
import javax.servlet.ServletContext
import org.scalatra.LifeCycle
import com.mongodb.casbah.MongoClient

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

class ScalaraTestFoo extends ScalatraServlet {
  get("/") {
    <html>
	Hello to scalatra
	</html>
  }
  
  get("/json-page") {
	  
	  "<json-response>Hello to scalatra</json-resonse>"
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
    context.mount(new ScalaraTestFoo, "/*")
  }
}