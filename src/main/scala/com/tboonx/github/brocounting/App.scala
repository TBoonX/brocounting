package com.tboonx.github.brocounting

import java.util.NoSuchElementException
import javax.servlet.ServletContext

import com.mongodb.casbah.TypeImports
import com.mongodb.{casbah, DBObject}
import com.mongodb.casbah.commons.{Imports, MongoDBObjectBuilder, MongoDBObject}
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

  def updateSession(userName: String) : Session = {
    val newAcquiredSession: Session = Session(userName)
    val userQuery: Imports.DBObject = MongoDBObject("user" -> userName)
    db("session").update(userQuery, grater[Session].asDBObject(newAcquiredSession), upsert = true)
    response.setHeader("hash", newAcquiredSession.hash)
    newAcquiredSession
  }

  def handleSession(hash : String)(verifiedSession : (Session => String)): String = {
    val sessionInDb: Option[DBObject] = checkForHash(hash)
    if(sessionInDb.isDefined){
      val oldSession: Session = grater[Session].asObject(sessionInDb.get)
      val newSession: Session = updateSession(oldSession.user)
      response.setHeader("hash", newSession.hash)
      verifiedSession(oldSession)
    } else {
      notAuthorized
    }
  }

  def checkForHash(hash : String): Option[DBObject] = {
    val sessionQueryObject: com.mongodb.casbah.commons.Imports.DBObject = MongoDBObject("hash" -> hash)
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

  /**
   *  Handles the creation of a user
   *  The user
   */
  override def createCallback(givenId : String, bodyInstance : User, mongoInstance : => DBObject) : String = {
    def ensureResourceAndUsernameAreTheSame(instance : DBObject) = {
      if (givenId != bodyInstance.name.getOrElse("")) {
        instance.put("_id", givenId)
      }
    }
    def isUserIdIsAvailable(requestedUserId : String) = {
      db("user").findOne(createIdQuery(requestedUserId).result()).isEmpty
    }
    val instance: DBObject = mongoInstance
    ensureResourceAndUsernameAreTheSame(instance)
    if(isUserIdIsAvailable(givenId)){
      hashPasswordInDbObject(instance)
      db("user").insert(instance)
      val session: Session = Session(givenId)
      db("session").insert(grater[Session].asDBObject(session))
      response.setHeader("hash", session.hash.toString)
      givenId
    } else {
      response.setStatus(400)
      "the user id is already in use!"
    }
  }

  override def readCallback(givenId : String) : String = {
    handleSession(params("hash")) { session =>
      val query: com.mongodb.casbah.commons.Imports.DBObject = createIdQuery(givenId).result()
      db("user").findOne(query, MongoDBObject(passwordFieldName -> 0)).get.toString
    }
  }

  override def updateCallback(givenId: String, bodyInstance : => User, mongoInstance : => DBObject) : String = {
    handleSession(params("hash")) { session =>
      val pwHash: String = params("pw_hash")
      val instance: DBObject = mongoInstance
      hashPasswordInDbObject(instance)
      db("user").update(buildUserQuery(givenId, pwHash), instance).toString
    }
  }
  
  override def deleteCallback(givenId: String): String = {
    handleSession(params("hash")){ session =>
      val pwHash: String = params("pw_hash")
      val removeResult: TypeImports.WriteResult = db("user").remove(buildUserQuery(givenId, pwHash))
      val sucessfulRemoved: Boolean = removeResult.getN == 1
      if(sucessfulRemoved){
        db("session").remove(MongoDBObject("hash" ->session.hash))
        "you are removed from the server"
      } else {
        response.setStatus(400)
        "the removable of the user was not successful!"
      }
    }
  }

  def buildUserQuery(userName: String, pwHash: String) = {
    val userQueryBuilder = createIdQuery(userName) += (passwordFieldName -> pwHash)
    userQueryBuilder.result()
  }

}

class AccountService extends ScalaraRestfulApiDef with CRUDSupport[Account] {

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
  override implicit val mf: Manifest[Account] = Manifest.classType[Account](classOf[Account])

  override def readCallback(givenId: String): String = ???

  override def deleteCallback(givenId: String): String = ???

  override def updateCallback(givenId: String, bodyInstance: => Account, mongoInstance: => DBObject): String = ???

  override def createCallback(givenId: String, bodyInstance: Account, mongoInstance: => DBObject): String = ???
}

class LoginServlet extends ScalaraRestfulApiDef {
  post("/"){
    val bodyInstance: User = parsedBody.extract[User]
    if(bodyInstance.name.isDefined) {
      val userName: String = bodyInstance.name.get
      val userInDB: Option[casbah.MongoCollection#T] = db("user").findOne(MongoDBObject("_id" -> userName))
      if (userInDB.get.get("password") == MD5.hash(bodyInstance.password)) {
        updateSession(userName)
        "session acquired"
      } else {
        notAuthorized
      }
    } else {
      response.setStatus(400)
      "the body needs to have an user name!"
    }
  }
}

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext): Unit = {
    val servicePrefix = "/service"
    context.mount(new ScalaraRestfulApiDef, "%s/*".format(servicePrefix))
    context.mount(new UserService, "%s/user/*".format(servicePrefix))
    context.mount(new AccountService, "%s/account/*".format(servicePrefix))
    context.mount(new LoginServlet, "%s/login/*".format(servicePrefix))
  }
}