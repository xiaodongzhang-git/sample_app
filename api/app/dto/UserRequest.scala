package dto

import play.api.libs.json.{Json, Reads}

case class UserRequest(username: Option[String], password: Option[String], role: Option[Int], status: Option[Int])

object UserRequest {
  implicit val reads: Reads[UserRequest] = Json.reads[UserRequest]
}
