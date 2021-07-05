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
import akka.http.scaladsl.server._
import org.iv.EmpServer.route

import scala.concurrent.Future

/**
 * Created by twr143 on 05.07.2021 at 13:54.
 */
class ClientServiceTest extends AnyWordSpec with MockFactory with Matchers with ScalatestRouteTest with BeforeAndAfterAll with ScalaFutures{
  //  val mock: HttpExt = mock[HttpExt]
  //  (mock.singleRequest _).expects(,
  //    "", "create"),*,*,*)
  //    .returning(Future.successful(HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, CreateResponse("created").toJson.toString))))
  //  val service = new EmpService(mock, "http://localhost:8081/")
  implicit val mockSendRequest = mockFunction[HttpRequest, Future[HttpResponse]]
  val service = new EmpService(Http(),"http://localhost:8081/")
    mockSendRequest.expects(*).onCall({req: HttpRequest =>
          if (req.method == HttpMethods.POST && req.uri.toString.endsWith("/create")) {
            Future.successful(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, CreateResponse("created").toJson.toString)))
          } else {
            Future.successful(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity("bad request")))
          }
        })

 val r = route(service)

  "client layer" should {
    "create first employee" in {
        Post("/create", EmployeeJson("igor", "2021-07-05T03:12:13")) ~> r ~> check {
          responseAs[CreateResponse].result shouldEqual "created"
        }
      }

    }


}