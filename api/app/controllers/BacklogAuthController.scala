package controllers

import javax.inject._
import play.api.libs.json._
import dto.{ApiResponse}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}
import services.BacklogAuthService
import utils.JwtAuthAction

@Singleton
class BacklogAuthController @Inject()(authService: BacklogAuthService,
                                   val controllerComponents: ControllerComponents,
                                   jwtAuthAction: JwtAuthAction)
                                  (implicit ec: ExecutionContext)
  extends BaseController{

  def getAccessToken = jwtAuthAction.async(parse.json) { request =>
    (request.body \ "code").asOpt[String] match {
      case Some(code) =>
        authService.fetchAccessToken(code).map {
          case Right(accessToken) => Ok(Json.toJson(ApiResponse.success(Json.obj("access_token" -> accessToken))))
          case Left(error)        => BadRequest(Json.toJson(ApiResponse.error(400, "error")))
        }
      case None =>
        Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, "Invalid JSON"))))
    }
  }
}
