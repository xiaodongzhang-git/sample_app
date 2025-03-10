
package dto

import play.api.libs.json.{Json, Reads, __}
import play.api.libs.functional.syntax._

case class IssueRequest(
  summary: String,
  issueTypeId: Int,
  description: String,
  startDate: String,
  dueDate: String,
  wikiContent: String,
  volunteerIds: String,
)

object IssueRequest {
  implicit val issueRequestReads: Reads[IssueRequest] = (
    (__ \ "summary").read[String] and
    (__ \ "issueTypeId").read[Int] and
    (__ \ "description").read[String] and
    (__ \ "startDate").read[String] and
    (__ \ "dueDate").read[String] and
    (__ \ "wikiContent").read[String] and
    (__ \ "volunteerIds").read[String]
  )(IssueRequest.apply _)
}
