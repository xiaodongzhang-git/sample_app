
package controllers

import javax.inject._
import play.api.Configuration
import services.{ BacklogIssueService,IssueService }
import dto.{ApiResponse, IssueRequest}
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import utils.JwtAuthAction

@Singleton
class IssueController @Inject()(backlogIssueService: BacklogIssueService,
                                issueService: IssueService,
                                val controllerComponents: ControllerComponents,
                                jwtAuthAction: JwtAuthAction)
                               (implicit ec: ExecutionContext)
  extends BaseController {

  implicit val apiResponseWrites: OWrites[ApiResponse[JsObject]] = Json.writes[ApiResponse[JsObject]]
  implicit val issueListWrites: OWrites[ApiResponse[Seq[JsObject]]] = Json.writes[ApiResponse[Seq[JsObject]]]

  /** （POST /issues） - admin only */
  def addIssue = jwtAuthAction.async(parse.json) { request =>
    val role = request.attrs.get(JwtAuthAction.UserRole).getOrElse("2")

    if (role != "1") {
      Future.successful(Forbidden(Json.toJson(ApiResponse.error(403, "Insufficient permissions, the operator cannot add issues"))))
    } else {
      request.body.validate[IssueRequest] match {
        case JsSuccess(issueRequest, _) =>
          backlogIssueService.createIssue(issueRequest.summary, issueRequest.issueTypeId, issueRequest.description, issueRequest.startDate, issueRequest.dueDate).flatMap { backlogResponse =>
            backlogResponse.fold(
              error => Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, error.mkString(", "))))),
              backlogResponse => {
                println(backlogResponse)
                val issueId = (backlogResponse \ "id").as[Long]  // Assuming ID is returned in the response
                issueService.createIssue(issueId, issueRequest.wikiContent, issueRequest.volunteerIds).map { _ =>
                  Ok(Json.toJson(ApiResponse.success(Json.obj("create" -> true))))
                }
              }
            )
          }
        case JsError(errors) =>
          Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, "Invalid JSON"))))
      }
    }
  }

  /** （GET /issues） - all */
  def getIssues = jwtAuthAction.async { request =>
    backlogIssueService.listIssues().flatMap { response =>
      response.fold(
        error => Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, error.mkString(", "))))),
        issuesFromAPI => {
          issueService.listIssues().map { localIssues =>
            val localIssuesMap = localIssues.map(issue => issue.issueId -> (issue.wikiContent, issue.volunteerIds)).toMap
            val enrichedIssues = issuesFromAPI.map { issue =>
              val issueId = (issue \ "id").as[Long]
              val localData = localIssuesMap.getOrElse(issueId, ("", ""))
              Json.obj(
                "id" -> issueId,
                "summary" -> (issue \ "summary").as[String],
                "description" -> (issue \ "description").as[String],
                "issueTypeId" -> (issue \ "issueTypeId").as[Int],
                "issueTypeName" -> (issue \ "issueTypeName").as[String],
                "startDate" -> (issue \ "startDate").as[String],
                "dueDate" -> (issue \ "dueDate").as[String],
                "wikiContent" -> localData._1,
                "volunteerIds" -> localData._2
              ) // Add other necessary fields from issue
            }
            Ok(Json.toJson(ApiResponse.success(enrichedIssues)))
          }
        }
      )
    }
  }
  

  /** （PATCH /issues/:id） - admin only */
  def updateIssue(id: Int) = jwtAuthAction.async(parse.json) { request =>
    val role = request.attrs.get(JwtAuthAction.UserRole).getOrElse("2")

    if (role != "1") {
      Future.successful(Forbidden(Json.toJson(ApiResponse.error(403, "Insufficient permissions, the operator cannot edit issues"))))
    } else {
      request.body.validate[IssueRequest] match {
        case JsSuccess(issueRequest, _) =>
          backlogIssueService.updateIssue(id, issueRequest.summary, issueRequest.issueTypeId, issueRequest.description, issueRequest.startDate, issueRequest.dueDate).flatMap { response =>
            response.fold(
              error => Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, error.mkString(", "))))),
              _ => issueService.updateIssue(id.toLong, issueRequest.wikiContent, issueRequest.volunteerIds).map { _ =>
                Ok(Json.toJson(ApiResponse.success(Json.obj("updated" -> true))))
              }
            )
          }
        case JsError(errors) =>
          Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, "Invalid JSON"))))
      }
    }
  }

}
