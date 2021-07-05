package org.iv.test

import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}
import com.sksamuel.elastic4s.http.JavaClient
import org.iv.{EmpDaoServer, EmpDaoService}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.iv.JsonSupport._
import org.scalatest.BeforeAndAfterAll

/**
 * Created by twr143 on 05.07.2021 at 9:01.
 */
class EmpServiceTest extends AnyWordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterAll {
  val service = new EmpDaoService(ElasticClient(JavaClient(ElasticProperties("http://localhost:9200"))), "test7")
  val route = EmpDaoServer.route(service)

  "The employee dao service" should {

    "create first employee" in {
      Post("/create", EmployeeJson("igor", "2021-07-05T03:12:13")) ~> route ~> check {
        responseAs[CreateResponse].result shouldEqual "created"
      }
    }
    "create second employee" in {
      Post("/create", EmployeeJson("ilya", "2021-07-05T03:12:13")) ~> route ~> check {
        responseAs[CreateResponse].result shouldEqual "created"
      }
    }

    "verify that two employees are stored" in {
      Post("/query", QueryJson("name:*")) ~> route ~> check {
        responseAs[QueryResponse].records.size shouldEqual 2
      }
    }
    "modify both employees, adding '2' to their names" in {
      Post("/update", UpdateJson("name:i*", "ctx._source.name = ctx._source.name + '2'")) ~> route ~> check {
        responseAs[UpdateDeleteResponse].numRecords shouldEqual 2
      }
    }
    "verify that both names has '2' at the end" in {
      Post("/query", QueryJson("name:*")) ~> route ~> check {
        responseAs[QueryResponse].records.map(_.name).forall(_.last == '2') shouldEqual true
      }
    }

    "delete by query both employees" in {
      Post("/delete", DeleteJson("name:i*")) ~> route ~> check {
        responseAs[UpdateDeleteResponse].numRecords shouldEqual 2
      }
    }
    "verify that no employees left" in {
      Post("/query", QueryJson("name:*")) ~> route ~> check {
        responseAs[QueryResponse].records.size shouldEqual 0
      }
    }
  }

  override def afterAll(): Unit = {
    super.afterAll()
    service.terminateConnection()
  }
}
