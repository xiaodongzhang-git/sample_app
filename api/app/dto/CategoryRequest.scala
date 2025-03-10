package dto

import play.api.libs.json.{Json, Reads}

case class CategoryRequest(name: String)

object CategoryRequest {
  implicit val reads: Reads[CategoryRequest] = Json.reads[CategoryRequest]
}
