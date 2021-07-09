package org.iv.test

import org.scalatest.wordspec.AnyWordSpec
import akka.http.scaladsl.Http
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.iv.EmpService
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import akka.http.scaladsl.model._
import org.iv.JsonSupport._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.model.StatusCodes._
import org.iv.EmpServer.route
import org.iv.model.Employee

import scala.concurrent.Future

/**
 * Created by twr143 on 05.07.2021 at 13:54.
 */
class ClientServiceTest extends AnyWordSpec with MockFactory with Matchers with ScalatestRouteTest with BeforeAndAfterAll with ScalaFutures {
  implicit val mockSendRequest = mockFunction[HttpRequest, Future[HttpResponse]]
  val service = new EmpService(Http(), "http://localhost:8081/")


  val r = route(service)

  "client layer" should {
    "create employee" in {
      val resp = HttpResponse(OK, entity = HttpEntity(ContentTypes.`application/json`, CreateResponse("created").toJson.toString))
      mockSendRequest.expects(where { r: HttpRequest => r.uri.toString.endsWith("create") }).onCall({ _:Any => Future.successful(resp) })
      Post("/create", EmployeeJson("igor", "2021-07-05T03:12:13")) ~> r ~> check {
        responseAs[CreateResponse].result shouldEqual "created"
      }
    }
    "return validation error if create employee doesn't pass validation" in {
      Post("/create", EmployeeJson("i", "2021-07-05T03:12:13")) ~> r ~> check {
        response.status shouldEqual BadRequest
      }
    }
    "list employees by query" in {
      val resp = HttpResponse(OK, entity = HttpEntity(ContentTypes.`application/json`, QueryResponse(List(Employee("", ""))).toJson.toString))
      mockSendRequest.expects(where { r: HttpRequest => r.uri.toString.endsWith("query") }).onCall({ _:Any => Future.successful(resp) })
      Post("/query", QueryJson("name:*")) ~> r ~> check {
        responseAs[QueryResponse].records.size shouldEqual 1
      }
    }
    "update by query" in {
      val resp = HttpResponse(OK, entity = HttpEntity(ContentTypes.`application/json`, UpdateDeleteResponse(2).toJson.toString))
      mockSendRequest.expects(where { r: HttpRequest => r.uri.toString.endsWith("update") }).onCall({ _:Any => Future.successful(resp) })
      Post("/update", UpdateJson("name:*", "")) ~> r ~> check {
        responseAs[UpdateDeleteResponse].numRecords shouldEqual 2
      }
    }
    "delete by query" in {
      val resp = HttpResponse(OK, entity = HttpEntity(ContentTypes.`application/json`, UpdateDeleteResponse(2).toJson.toString))
      mockSendRequest.expects(where { r: HttpRequest => r.uri.toString.endsWith("delete") }).onCall({_:Any => Future.successful(resp)})
      Post("/delete", DeleteJson("name:*")) ~> r ~> check {
        responseAs[UpdateDeleteResponse].numRecords shouldEqual 2
      }
    }
  }


}