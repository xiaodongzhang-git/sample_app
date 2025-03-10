package utils

import javax.inject.Inject
import play.api.mvc._
import play.api.libs.json.Json
import play.api.mvc.request.RequestAttrKey
import scala.concurrent.{ExecutionContext, Future}
import dto.ApiResponse

object JwtAuthAction {
  val UserRole = RequestAttrKey.Server
}

class JwtAuthAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    request.headers.get("Authorization") match {
      case Some(authHeader) if authHeader.startsWith("Bearer ") =>
        val token = authHeader.substring(7)
        JwtUtils.verifyToken(token) match {
          case Some(payload) =>
            val newRequest = request.addAttr(JwtAuthAction.UserRole, payload.role)
            block(newRequest)
          case None => Future.successful(Results.Unauthorized(Json.toJson(ApiResponse.error(401, "Invalid or expired token"))))
        }
      case _ => Future.successful(Results.Unauthorized(Json.toJson(ApiResponse.error(401, "Missing token"))))
    }
  }
}
