package dto

import play.api.libs.json.{Json, Reads}

case class IssueTypeRequest(name: String)

object IssueTypeRequest {
  implicit val reads: Reads[IssueTypeRequest] = Json.reads[IssueTypeRequest]
}
