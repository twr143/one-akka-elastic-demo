package org.iv

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.iv.model.Employee
import spray.json.DefaultJsonProtocol
import validation.Validator._

/**
 * Created by twr143 on 03.07.2021 at 8:31.
 */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  final case class EmployeeJson(name: String, joined: String){
    def toEmployee() = Employee(name,joined)
  }
  final case class QueryJson(query: String)
  final case class DeleteJson(query: String)
  final case class UpdateJson(query: String, script:String)

  implicit val employeeFormat = jsonFormat2(EmployeeJson)
  implicit val employeeBeanFormat = jsonFormat2(Employee)
  implicit val queryFormat = jsonFormat1(QueryJson)
  implicit val deleteFormat = jsonFormat1(DeleteJson)
  implicit val updateFormat = jsonFormat2(UpdateJson)
  implicit val validationError = jsonFormat2(ValidationError)
}
