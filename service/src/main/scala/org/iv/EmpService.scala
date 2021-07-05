package org.iv


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse}



import spray.json._
import scala.concurrent.{ExecutionContext, Future}
import org.iv.JsonSupport._
import akka.http.scaladsl.model.StatusCodes._

/**
 * Created by twr143 on 03.07.2021 at 7:23.
 */
class EmpService(implicit ec: ExecutionContext, system: ActorSystem) {
  lazy val client = Http()
  val uri = "http://localhost:8081/"

  def create(e: EmployeeJson): Future[HttpResponse] =
    client.singleRequest(mkRequest(e.toJson, "create")).recover(recoverPf)

  def deletebyQuery(q: DeleteJson): Future[HttpResponse] =
    client.singleRequest(mkRequest(q.toJson, "delete")).recover(recoverPf)

  def queryEmployees(q: QueryJson): Future[HttpResponse] =
    client.singleRequest(mkRequest(q.toJson, "query")).recover(recoverPf)

  def updateByQ(u:UpdateJson): Future[HttpResponse] =
    client.singleRequest(mkRequest(u.toJson, "update")).recover(recoverPf)

  private def mkRequest(jv: JsValue, uripath: String) =
    HttpRequest(
      method = HttpMethods.POST,
      uri = uri + uripath,
      entity = HttpEntity(ContentTypes.`application/json`, jv.toString)
    )

  private def recoverPf: PartialFunction[Throwable, HttpResponse] = {
    case _: akka.stream.StreamTcpException => HttpResponse(ServiceUnavailable, entity = "dao server unavailable")
  }

}
