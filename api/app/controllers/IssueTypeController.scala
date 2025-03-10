package controllers

import javax.inject._
import play.api.Configuration
import services.BacklogIssueTypeService
import dto.{ApiResponse, IssueTypeRequest}
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import utils.JwtAuthAction

@Singleton
class IssueTypeController @Inject()(backlogIssueTypeService: BacklogIssueTypeService,
                                    val controllerComponents: ControllerComponents,
                                    jwtAuthAction: JwtAuthAction)
                                   (implicit ec: ExecutionContext)
  extends BaseController {

  implicit val apiResponseWrites: OWrites[ApiResponse[JsObject]] = Json.writes[ApiResponse[JsObject]]
  implicit val issueTypeListWrites: OWrites[ApiResponse[Seq[JsObject]]] = Json.writes[ApiResponse[Seq[JsObject]]]

  /** （POST /issueTypes） - admin only */
  def addIssueType = jwtAuthAction.async(parse.json) { request =>
    val role = request.attrs.get(JwtAuthAction.UserRole).getOrElse("2")

    if (role != "1") { // Assuming "1" is admin role
      Future.successful(Forbidden(Json.toJson(ApiResponse.error(403, "Insufficient permissions, the operator cannot add issue types"))))
    } else {
      request.body.validate[IssueTypeRequest].map { issueTypeRequest =>
        backlogIssueTypeService.createIssueType(issueTypeRequest.name).map { response =>
          response.fold(
            error => BadRequest(Json.toJson(ApiResponse.error(400, error.mkString(", ")))),
            issueType => Ok(Json.toJson(ApiResponse.success(issueType)))
          )
        }
      }.getOrElse(Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, "Invalid JSON")))))
    }
  }

  /** （GET /issueTypes） - all */
  def getIssueTypes = jwtAuthAction.async { request =>

    backlogIssueTypeService.listIssueTypes().map { response =>
      response.fold(
        error => BadRequest(Json.toJson(ApiResponse.error(400, error.mkString(", ")))),
        issueTypes => Ok(Json.toJson(ApiResponse.success(issueTypes)))
      )
    }
  }

  /** （PATCH /issueTypes/:id） - admin only */
  def updateIssueType(id: Int) = jwtAuthAction.async(parse.json) { request =>
    val role = request.attrs.get(JwtAuthAction.UserRole).getOrElse("2")

    if (role != "1") {
      Future.successful(Forbidden(Json.toJson(ApiResponse.error(403, "Insufficient permissions, the operator cannot edit issue types"))))
    } else {
      request.body.validate[IssueTypeRequest].map { issueTypeRequest =>
        backlogIssueTypeService.updateIssueType(id, issueTypeRequest.name).map { response =>
          response.fold(
            error => BadRequest(Json.toJson(ApiResponse.error(400, error.mkString(", ")))),
            issueType => Ok(Json.toJson(ApiResponse.success(issueType)))
          )
        }
      }.getOrElse(Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, "Invalid JSON")))))
    }
  }
}
