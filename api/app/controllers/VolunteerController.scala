package controllers

import javax.inject._
import services.VolunteerService
import dto.{ApiResponse, VolunteerRequest, UpdateVolunteerRequest}
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import utils.JwtAuthAction

@Singleton
class VolunteerController @Inject()(volunteerService: VolunteerService,
                                    val controllerComponents: ControllerComponents,
                                    jwtAuthAction: JwtAuthAction)
                                   (implicit ec: ExecutionContext)
  extends BaseController {

  implicit val volunteerWrites: OWrites[ApiResponse[JsObject]] = Json.writes[ApiResponse[JsObject]]
  implicit val volunteerListWrites: OWrites[ApiResponse[Seq[JsObject]]] = Json.writes[ApiResponse[Seq[JsObject]]]

  /** Add a new volunteer (POST /volunteers) - admin only */
  def addVolunteer = jwtAuthAction.async(parse.json) { request =>
    val role = request.attrs.get(JwtAuthAction.UserRole).getOrElse("2")

    if (role == "2") {
      Future.successful(Forbidden(Json.toJson(ApiResponse.error(403, "Insufficient permissions, operators cannot add volunteers"))))
    } else {
      request.body.validate[VolunteerRequest].map { volunteerRequest =>
        volunteerService.createVolunteer(volunteerRequest.name, volunteerRequest.email, volunteerRequest.phoneNumber, volunteerRequest.gender).map { count =>
          Ok(Json.toJson(ApiResponse.success(Json.obj("count" -> count))))
        }
      }.getOrElse(Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, "Invalid JSON")))))
    }
  }

  /** Get all volunteers (GET /volunteers) - all */
  def getVolunteers = jwtAuthAction.async { request =>
    volunteerService.getAllVolunteers().map { volunteers =>
      val volunteerJson = volunteers.map { volunteer =>
        Json.obj(
          "id" -> volunteer.id,
          "name" -> volunteer.name,
          "email" -> volunteer.email,
          "phoneNumber" -> volunteer.phoneNumber,
          "gender" -> volunteer.gender,
          "status" -> volunteer.status
        )
      }
      Ok(Json.toJson(ApiResponse.success(volunteerJson)))
    }
  }

  /** Update a volunteer (PUT /volunteers/:id) - admin only */
  def updateVolunteer(id: Int) = jwtAuthAction.async(parse.json) { request =>
    val role = request.attrs.get(JwtAuthAction.UserRole).getOrElse("2")

    if (role == "2") {
      Future.successful(Forbidden(Json.toJson(ApiResponse.error(403, "Insufficient permissions, operators cannot update volunteers"))))
    } else {
      request.body.validate[UpdateVolunteerRequest].map { updateVolunteerRequest =>
        volunteerService.updateVolunteer(id, updateVolunteerRequest.name, updateVolunteerRequest.email, updateVolunteerRequest.phoneNumber, updateVolunteerRequest.gender, updateVolunteerRequest.status).map { rowsUpdated =>
          if (rowsUpdated > 0)
            Ok(Json.toJson(ApiResponse.success(Json.obj("updated" -> true))))
          else
            NotFound(Json.toJson(ApiResponse.error(404, "Volunteer not found")))
        }
      }.getOrElse(Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, "Invalid JSON")))))
    }
  }
}
