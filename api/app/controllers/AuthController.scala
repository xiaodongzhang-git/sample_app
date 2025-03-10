package controllers

import javax.inject._
import services.AuthService
import dto.{AuthRequest, ApiResponse}
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthController @Inject()(authService: AuthService,
                               val controllerComponents: ControllerComponents)
                              (implicit ec: ExecutionContext)
  extends BaseController {

  implicit val loginRequestReads: Reads[AuthRequest] = Json.reads[AuthRequest]
  implicit val apiResponseWrites: OWrites[ApiResponse[JsObject]] = Json.writes[ApiResponse[JsObject]]

  def login = Action.async(parse.json) { request =>
    request.body.validate[AuthRequest].map { loginData =>
      authService.login(loginData.username, loginData.password).map {
        case Some(token) => Ok(Json.toJson(ApiResponse.success(Json.obj("token" -> token, "nub_url" -> "https://nulab-exam.backlog.jp/OAuth2AccessRequest.action?response_type=code&client_id=PFpoOe1UXAEVlpa4rojDvF5WzT3uMTyT&redirect_uri=http%3A%2F%2Flocalhost%3A3000%2F&state=001"))))
        case None => Unauthorized(Json.toJson(ApiResponse.error(401, "Invalid credentials.")))
      }
    }.getOrElse(Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, "Invalid JSON")))))
  }
}
