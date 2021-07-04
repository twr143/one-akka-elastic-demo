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
import org.iv.JsonSupport._


// use it wherever json (un)marshalling is needed
object EmpServer extends Directives {
  def route(service: EmpService) = {
    handleExceptions(serviceExceptionHandler) {
      post {
        concat(
          path("create") {
            entity(as[EmployeeJson]) { e => // will unmarshal JSON to Order
              val errors = Validator.validateEmployee(e.name, e.joined)
              if (errors.nonEmpty)
                complete(BadRequest, errors)
              else
                onSuccess(service.create(e))(complete(_))
            }
          },
          path("query") {
            entity(as[QueryJson]) { e =>
              onSuccess(service.queryEmployees(e))(complete(_))
            }
          },
          path("delete") {
            entity(as[DeleteJson]) { e =>
              onSuccess(service.deletebyQuery(e))(complete(_))
            }
          },
          path("update") {
            entity(as[UpdateJson]) { e =>
              onSuccess(service.updateByQ(e))(complete(_))
            }
          }
        )
      }
    }
  }

  lazy val serviceExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: UnsupportedOperationException =>
        complete(HttpResponse(NotImplemented, entity = e.getMessage))
      case e: RuntimeException =>
        complete(HttpResponse(InternalServerError, entity = "unknow happened " + e))
    }

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val executionContext = system.dispatcher
    val service = new EmpService()
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route(service))
    println(s"Employee server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => {
        system.terminate()
      }) // and shutdown when done
  }


}