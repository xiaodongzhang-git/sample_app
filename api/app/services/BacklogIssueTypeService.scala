package services

import javax.inject._
import play.api.libs.functional.syntax._
import play.api.Configuration
import play.api.libs.json._
import play.api.libs.ws._
import scala.concurrent.{ExecutionContext, Future}
import utils.RedisClient

@Singleton
class BacklogIssueTypeService @Inject()(ws: WSClient, config: Configuration)(implicit ec: ExecutionContext) {

  private val backlogApiUrl = config.get[String]("backlog.api.url")
  private val redisKey = config.get[String]("backlog.redis.key")
  private val projectId = config.get[Int]("backlog.project.id")

  def listIssueTypes(): Future[JsResult[Seq[JsObject]]] = {
    val url = s"$backlogApiUrl/projects/$projectId/issueTypes"
    val token = RedisClient.get(redisKey)


    ws.url(url)
      .withHttpHeaders(
        "Authorization" -> s"Bearer $token",
        "Content-Type" -> "application/json"
      )
      .get()
      .map { response =>
        Json.parse(response.body).validate[Seq[JsObject]](
          Reads.seq(
            ( (__ \ "id").read[Int] and
              (__ \ "name").read[String]
            )((id, name) => Json.obj("id" -> id, "name" -> name))
          )
        )
      }
  }



  def createIssueType(name: String, color: String = "#e30000"): Future[JsResult[JsObject]] = {
    val url = s"$backlogApiUrl/projects/$projectId/issueTypes"
    val token = RedisClient.get(redisKey)
    val data = Map("name" -> Seq(name), "color" -> Seq(color))

    ws.url(url)
      .withHttpHeaders(
        "Authorization" -> s"Bearer $token",
        "Content-Type" -> "application/x-www-form-urlencoded"
      )
      .post(data)
      .map { response =>
        Json.parse(response.body).validate[JsObject]
      }
  }

  def updateIssueType(issueTypeId: Int, name: String, color: String = "#e30000"): Future[JsResult[JsObject]] = {
    val url = s"$backlogApiUrl/projects/$projectId/issueTypes/$issueTypeId"
    val token = RedisClient.get(redisKey)
    val data = Map("name" -> Seq(name), "color" -> Seq(color))

    ws.url(url)
      .withHttpHeaders(
        "Authorization" -> s"Bearer $token",
        "Content-Type" -> "application/x-www-form-urlencoded"
      )
      .patch(data)
      .map { response =>
        Json.parse(response.body).validate[JsObject]
      }
  }
}
