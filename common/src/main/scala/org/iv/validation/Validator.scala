package org.iv.validation


/**
 * Created by twr143 on 03.07.2021 at 13:09.
 */
object Validator {
  def check[A](error: ValidationError, value: A)(f: A => Boolean) = if (f(value)) List.empty[ValidationError] else List(error)

  final case class ValidationError(fieldName: String, code: String)

  def validateEmployee(name: String, date: String): List[ValidationError] = {
      check(ValidationError("name", "at least 2 chars"), name)(_.length > 1) ++
      check(ValidationError("name", "max 15 chars"), name)(_.length <= 15) ++
      check(ValidationError("date", "valid local iso datetime, 2011-12-03T10:15:30"), date )(validateDfsdate)
  }

  def validateDfsdate(date: String): Boolean = try {
    val a = java.time.LocalDate.parse(date, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    true
  } catch {
    case ex: java.time.format.DateTimeParseException => {
      false
    }
  }
}