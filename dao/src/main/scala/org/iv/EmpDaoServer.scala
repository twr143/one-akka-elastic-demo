package org.iv

/**
 * Created by twr143 on 03.07.2021 at 8:19.
 */

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.HttpResponse
import org.iv.validation.Validator
import scala.io.StdIn

// use it wherever json (un)marshalling is needed
object EmpDaoServer extends Directives with JsonSupport {
  def route(service: EmpDaoService) = {
    handleExceptions(daoServiceExceptionHandler) {
      post {
        concat(
          path("create") {
            entity(as[EmployeeJson]) { e => // will unmarshal JSON to Order
              val errors = Validator.validateEmployee(e.name, e.joined)
              if (errors.nonEmpty)
                complete(BadRequest, errors)
              else
                onSuccess(service.insert(e.toEmployee))(r => complete(OK, CreateResponse(r)))
            }
          },
          path("query") {
            entity(as[QueryJson]) { e =>
              onSuccess(service.queryEmployees(e.query))(recs => complete(OK, QueryResponse(recs)))
            }
          },
          path("delete") {
            entity(as[DeleteJson]) { e =>
              onSuccess(service.deletebyQuery(e.query))(r => complete(OK, UpdateDeleteResponse(r)))
            }
          },
          path("update") {
            entity(as[UpdateJson]) { e =>
              onSuccess(service.updateByQ(e.query, e.script))(r => complete(OK, UpdateDeleteResponse(r)))
            }
          }
        )
      }
    }
  }

  lazy val daoServiceExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: UnsupportedOperationException =>
        complete(HttpResponse(NotImplemented, entity = e.getMessage))
      case e: RuntimeException if e.getMessage.startsWith("java.net.ConnectException") =>
        complete(HttpResponse(ServiceUnavailable, entity = "elastic unavailable"))
      case e: RuntimeException =>
        complete(HttpResponse(InternalServerError, entity = e.getMessage))
    }

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val executionContext = system.dispatcher
    val service = new EmpDaoService()
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route(service))
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => {
        service.terminateConnection()
        system.terminate()
      }) // and shutdown when done
  }


}