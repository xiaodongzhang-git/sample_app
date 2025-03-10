package dto

import play.api.libs.json.{Json, OWrites}

case class ApiResponse[T](code: Int, data: T, message: String)

object ApiResponse {
  implicit def jsonWrites[T: OWrites]: OWrites[ApiResponse[T]] = Json.writes[ApiResponse[T]]

  def success[T](data: T): ApiResponse[T] = ApiResponse(200, data, "")

  def error(code: Int, message: String): ApiResponse[Map[String, String]] =
    ApiResponse(code, Map.empty[String, String], message)
}
