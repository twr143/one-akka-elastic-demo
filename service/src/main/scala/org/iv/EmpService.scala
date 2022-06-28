package org.iv


import akka.actor.ActorSystem
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse}
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import org.iv.JsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import org.iv.EmpService._

/**
 * Created by twr143 on 03.07.2021 at 7:23.
 */
class EmpService(client: HttpExt, uri: String)(implicit ec: ExecutionContext, system: ActorSystem, send: HttpRequest => Future[HttpResponse] = client.singleRequest(_)) {

  def create(e: EmployeeJson): Future[HttpResponse] =
    executeRequest(mkRequest(e.toJson, uri, "create"))

  def deletebyQuery(q: DeleteJson): Future[HttpResponse] =
    executeRequest(mkRequest(q.toJson, uri, "delete"))

  def queryEmployees(q: QueryJson): Future[HttpResponse] =
    executeRequest(mkRequest(q.toJson, uri, "query"))

  def updateByQ(u: UpdateJson): Future[HttpResponse] =
    executeRequest(mkRequest(u.toJson, uri, "update"))


  private def recoverPf: PartialFunction[Throwable, HttpResponse] = {
    case _: akka.stream.StreamTcpException => HttpResponse(ServiceUnavailable, entity = "dao server unavailable")
  }

  private def executeRequest: HttpRequest => Future[HttpResponse] =
    a => Future.successful{ throw new RuntimeException("123")}
}
object EmpService {
  def mkRequest(jv: JsValue, uri: String, uripath: String) =
    HttpRequest(
      method = HttpMethods.POST,
      uri = uri + uripath,
      entity = HttpEntity(ContentTypes.`application/json`, jv.toString)
    )

}
