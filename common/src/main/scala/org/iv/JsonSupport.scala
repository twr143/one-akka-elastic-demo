package org.iv

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.iv.model.Employee
import spray.json.DefaultJsonProtocol
import validation.Validator._

import scala.annotation.nowarn

/**
 * Created by twr143 on 03.07.2021 at 8:31.
 */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  @nowarn final case class EmployeeJson(name: String, joined: String) {
    def toEmployee() = Employee(name, joined)
  }
  @nowarn final case class QueryJson(query: String)
  @nowarn final case class DeleteJson(query: String)
  @nowarn final case class UpdateJson(query: String, script: String)
  @nowarn final case class QueryResponse(records: List[Employee])
  @nowarn final case class UpdateDeleteResponse(numRecords: Long)
  @nowarn final case class CreateResponse(result: String)

  implicit val employeeFormat = jsonFormat2(EmployeeJson)
  implicit val employeeBeanFormat = jsonFormat2(Employee)
  implicit val queryFormat = jsonFormat1(QueryJson)
  implicit val deleteFormat = jsonFormat1(DeleteJson)
  implicit val updateFormat = jsonFormat2(UpdateJson)
  implicit val validationError = jsonFormat2(ValidationError)
  implicit val queryReponseFormat = jsonFormat1(QueryResponse)
  implicit val updateDeleteResponseFormat = jsonFormat1(UpdateDeleteResponse)
  implicit val createResponseFormat = jsonFormat1(CreateResponse)

}
object JsonSupport extends JsonSupport
