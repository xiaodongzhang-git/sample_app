package dto

import play.api.libs.json.{Json, Reads}

case class VolunteerRequest(
  name: String,
  email: String,
  phoneNumber: String,
  gender: Int
)

object VolunteerRequest {
  implicit val reads: Reads[VolunteerRequest] = Json.reads[VolunteerRequest]
}
