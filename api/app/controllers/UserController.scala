package controllers

import javax.inject._
import services.UserService
import dto.{ApiResponse, UserRequest}
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import utils.JwtAuthAction

@Singleton
class UserController @Inject()(userService: UserService,
                               val controllerComponents: ControllerComponents,
                               jwtAuthAction: JwtAuthAction)
                              (implicit ec: ExecutionContext)
  extends BaseController {

  implicit val userWrites: OWrites[ApiResponse[JsObject]] = Json.writes[ApiResponse[JsObject]]
  implicit val userListWrites: OWrites[ApiResponse[Seq[JsObject]]] = Json.writes[ApiResponse[Seq[JsObject]]]

  /** Add a new user (POST /users) - admin only */
  def addUser = jwtAuthAction.async(parse.json) { request =>
  val role = request.attrs.get(JwtAuthAction.UserRole).getOrElse("2")

  if (role == "2") {
    Future.successful(Forbidden(Json.toJson(ApiResponse.error(403, "Insufficient permissions, operators cannot add users"))))
  } else {
    request.body.validate[UserRequest].map { userRequest =>
      (userRequest.username, userRequest.password, userRequest.role) match {
        case (Some(username), Some(password), Some(role)) =>
          userService.createUser(username, password, role).map { count =>
            Ok(Json.toJson(ApiResponse.success(Json.obj("count" -> count))))
          }

        case _ =>
          Future.successful(InternalServerError(Json.toJson(ApiResponse.error(500, "Invalid parameters: username, password, and role cannot be empty"))))
      }
    }.getOrElse(Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, "Invalid JSON")))))
  }
}


  /** Get all users (GET /users) - all users */
  def getUsers = jwtAuthAction.async { request =>
    userService.getAllUsers().map { users =>
      val userJson = users.map { user =>
        Json.obj(
          "id" -> user.id,
          "username" -> user.username,
          "role" -> user.role,
          "status" -> user.status
        )
      }
      Ok(Json.toJson(ApiResponse.success(userJson)))
    }
  }

  /** Update a user (PUT /users/:id) - admin only */
  def updateUser(id: Int) = jwtAuthAction.async(parse.json) { request =>
    val role = request.attrs.get(JwtAuthAction.UserRole).getOrElse("2")

    if (role == "2") {
      Future.successful(Forbidden(Json.toJson(ApiResponse.error(403, "Insufficient permissions, operators cannot update users"))))
    } else {
      request.body.validate[UserRequest].map { userRequest =>
        userService.updateUser(id, userRequest.username, userRequest.password, userRequest.role, userRequest.status).map { rowsUpdated =>
          if (rowsUpdated > 0)
            Ok(Json.toJson(ApiResponse.success(Json.obj("updated" -> true))))
          else
            NotFound(Json.toJson(ApiResponse.error(404, "User not found")))
        }
      }.getOrElse(Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, "Invalid JSON")))))
    }
  }
}
