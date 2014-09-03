package com.tboonx.github.brocounting

import com.mongodb.DBObject
import com.novus.salat._
import org.json4s.Formats
import org.scalatra.{CorsSupport, ScalatraServlet}
import org.scalatra.json.JacksonJsonSupport
import com.novus.salat.global._


trait HttpMethodSupport extends ScalatraServlet {
  final val path = "/:%s"
  val resource : String = "id"
  val method : String
  def pathOfResource = path.format(resource)
}


trait CreateMethodSupport[T <: AnyRef] extends HttpMethodSupport with JacksonJsonSupport {
  implicit val mf : Manifest[T] = manifest[T]
  implicit val jsonFormats : Formats
  override val method = "POST"
  def createCallback(givenId : String, bodyInstance : T, mongoInstance : => DBObject) : String
  post(pathOfResource){
    val creatable = parsedBody.extract[T](jsonFormats, mf)
    createCallback(resource, creatable, grater[T].asDBObject(creatable))
  }
}

trait ReadMethodSupport extends HttpMethodSupport {
  override val method = "GET"
  def getCallback(givenId : String) : String
  get(pathOfResource) {
    getCallback(params(resource))
  }
}


trait UpdateMethodSupport[T <: AnyRef] extends HttpMethodSupport with JacksonJsonSupport with CorsSupport {
  implicit val mf : Manifest[T] = manifest[T]
  implicit val jsonFormats : Formats
  override val method = "PUT"
  def updateCallback(givenId : String, bodyInstance : T, mongoInstance : => DBObject) : String
  put(pathOfResource){
    val updatable = parsedBody.extract[T](jsonFormats, mf)
    updateCallback(resource, updatable, grater[T].asDBObject(updatable))
  }
}

trait DeleteMethodSupport extends HttpMethodSupport {
  override val method = "DELETE"
  def deleteCallback(givenId : String) : String
  delete(pathOfResource) {
    deleteCallback(params(resource))
  }
}

trait CRUDSupport[T <: AnyRef] extends HttpMethodSupport with CreateMethodSupport[T] with ReadMethodSupport with UpdateMethodSupport[T] with DeleteMethodSupport{
  override implicit val mf : Manifest[T] = manifest[T]
}