package com.tboonx.github.brocounting

import com.mongodb.DBObject
import com.novus.salat._
import com.novus.salat.global._
import org.json4s.Formats
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{CorsSupport, ScalatraServlet}

trait HttpMethodSupport extends ScalatraServlet {
  val path = "/:%s"
  val resource : String = "id"
  val method : String
  def pathOfResource = path.format(resource)
}

trait CreateMethodSupport[T <: AnyRef] extends HttpMethodSupport with JacksonJsonSupport {
  implicit val mf : Manifest[T]
  implicit val jsonFormats : Formats
  override val method = "POST"
  def createCallback(givenId : String, bodyInstance : T, mongoInstance : => DBObject) : String
  post(pathOfResource){
    val creatable = parsedBody.extract[T](jsonFormats, mf)
    createCallback(params(resource), creatable, grater[T].asDBObject(creatable))
  }
}

trait ReadMethodSupport extends HttpMethodSupport {
  override val method = "GET"
  def readCallback(givenId : String) : String
  get(pathOfResource) {
    readCallback(params(resource))
  }
}

trait UpdateMethodSupport[T <: AnyRef] extends HttpMethodSupport with JacksonJsonSupport with CorsSupport {
  implicit def mf : Manifest[T]
  implicit val jsonFormats : Formats
  override val method = "PUT"
  def updateCallback(givenId : String, bodyInstance : => T, mongoInstance : => DBObject) : String
  put(pathOfResource){
    def updatable = parsedBody.extract[T](jsonFormats, mf)
    updateCallback(params(resource), updatable, grater[T].asDBObject(updatable))
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
  override implicit val mf : Manifest[T]
}