
package services

import javax.inject._
import play.api.libs.json._
import play.api.libs.ws._
import play.api.Configuration
import scala.concurrent.{ExecutionContext, Future}
import utils.RedisClient

@Singleton
class BacklogAuthService @Inject()(ws: WSClient, config: Configuration)(implicit ec: ExecutionContext) {

  case class TokenResponse(access_token: String, token_type: String, expires_in: Int, refresh_token: String)
  implicit val tokenReads: Reads[TokenResponse] = Json.reads[TokenResponse]
  
  // To load according to different environments
  private val backlogApiUrl = config.get[String]("backlog.api.url")
  private val clientId = config.get[String]("backlog.client.id")
  private val clientSecret = config.get[String]("backlog.client.secret")
  private val redirectUri = config.get[String]("backlog.redirect.uri")
  private val redisKey = config.get[String]("backlog.redis.key")

  def fetchAccessToken(code: String): Future[Either[String, String]] = {
    val requestData = Map(
      "grant_type" -> Seq("authorization_code"),
      "code" -> Seq(code),
      "redirect_uri" -> Seq(redirectUri),
      "client_id" -> Seq(clientId),
      "client_secret" -> Seq(clientSecret)
    )

    ws.url(s"$backlogApiUrl/oauth2/token")
      .withHttpHeaders("Content-Type" -> "application/x-www-form-urlencoded")
      .post(requestData)
      .map { response =>
        println("Backlog API Response: " + response.body)

        response.json.validate[TokenResponse] match {
          case JsSuccess(token, _) =>
            RedisClient.set(redisKey, token.access_token, token.expires_in)
            Right(token.access_token)
          case JsError(errors) =>
            Left(s"Invalid response: $errors")
        }
      }
  }
}
