package dto

import play.api.libs.json.{Json, OFormat}

case class UpdateVolunteerRequest(
  name: Option[String],
  email: Option[String],
  phoneNumber: Option[String],
  gender: Option[Int],
  status: Option[Int]
)

object UpdateVolunteerRequest {
  implicit val updateVolunteerRequestFormat: OFormat[UpdateVolunteerRequest] = Json.format[UpdateVolunteerRequest]
}
