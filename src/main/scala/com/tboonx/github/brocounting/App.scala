package com.tboonx.github.brocounting

import java.util.NoSuchElementException
import javax.servlet.ServletContext

import com.mongodb.DBObject
import com.mongodb.casbah.commons.{MongoDBObjectBuilder, MongoDBObject}
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import com.tboonx.github.brocounting.model._
import org.joda.time.DateTime
import org.scalatra.{CorsSupport, LifeCycle, ScalatraServlet}
import scala.collection.mutable

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

  options("/*") {
	  response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
	  response.setHeader("Access-Control-Allow-Origin", "*")
	  response.setHeader("Access-Control-Allow-Methods", request.getHeader("Access-Control-Request-Method"))
  }


  def handleSession(hash : String, verifiedSession : (Session => String)): String = {
    val sessionInDb: Option[DBObject] = checkForHash(hash)
    if(sessionInDb.isDefined){
      val session: Session = grater[Session].asObject(sessionInDb.get)
      response.setHeader("hash", session.hash)
      session.acquiredAt = DateTime.now
      db("session").update(createIdQuery(session.hash).result(), grater[Session].asDBObject(session))
      verifiedSession(session)
    } else {
      notAuthorized
    }
  }

  def checkForHash(hash : String): Option[DBObject] = {
    val sessionQueryObject: com.mongodb.casbah.commons.Imports.DBObject = createIdQuery(hash).result()
    val dbSession: Option[MongoCollection#T] = db("session").findOne(sessionQueryObject)
    if(dbSession.isDefined){
      Option(dbSession.get)
    } else {
      None
    }
  }

  def createIdQuery(givenId: String): mutable.Builder[(String, Any), com.mongodb.casbah.commons.Imports.DBObject] = {
    MongoDBObject.newBuilder += ("_id" -> givenId)
  }

  protected def notAuthorized: String = {
    response.setStatus(403)
    "error, session is not valid!"
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

  put("/session") {
    val user = parsedBody.extract[User]
    db("user").insert(grater[User].asDBObject(user))
    println("finshed inserting testuser: "+user.name)
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

    new Session("session123", "user", DateTime.now)
  }

  get("/session") {
    params("hash") match {
      case "session123" => new Response(true)
      case _ => new Response(false)
    }
  }
}

class UserService extends ScalaraRestfulApiDef with CRUDSupport[User] {

  override implicit val mf : Manifest[User] = Manifest.classType[User](classOf[User])

  def createPWHash(pw : String) : String = {
    MD5.hash(pw)
  }

  def hashPasswordInDbObject(instance: DBObject) {
    instance.put(passwordFieldName, createPWHash(instance.get(passwordFieldName).toString))
  }

  override def createCallback(givenId : String, bodyInstance : User, mongoInstance : => DBObject) : String = {
    val returnable: Session = Session(givenId)
    val instance: DBObject = mongoInstance
    hashPasswordInDbObject(instance)
    db("user").insert(instance)
    db("session").insert(grater[Session].asDBObject(returnable))
    returnable.hash.toString
  }

  override def readCallback(givenId : String) : String = {
    val hash: String = params("hash")
    handleSession(hash, { session =>
      val query: com.mongodb.casbah.commons.Imports.DBObject = createIdQuery(givenId).result()
      db("user").findOne(query, MongoDBObject(passwordFieldName -> 0)).get.toString
    })
  }

  override def updateCallback(givenId: String, bodyInstance : => User, mongoInstance : => DBObject) : String = {
    handleSession(params("hash"), { session =>
      val pwHash: String = params("pw_hash")
      val instance: DBObject = mongoInstance
      hashPasswordInDbObject(instance)
      db("user").update(buildUserQuery(givenId, pwHash), instance).toString
    })
  }
  
  override def deleteCallback(givenId: String): String = {
    val sessionHash: String = params("hash")
    val sessionInDb: Option[DBObject] = checkForHash(sessionHash)
    if(sessionInDb.isDefined){
      val pwHash: String = params("pw_hash")
      val users = db("user")
      users.remove(buildUserQuery(givenId, pwHash)).toString   
    } else {
      notAuthorized
    }
  }

  def buildUserQuery(userName: String, pwHash: String) = {
    val userQueryBuilder = createIdQuery(userName) += (passwordFieldName -> pwHash)
    userQueryBuilder.result()
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