package services

import javax.inject._
import play.api.libs.json._
import play.api.libs.ws._
import play.api.libs.functional.syntax._
import play.api.Configuration
import scala.concurrent.{ExecutionContext, Future}
import utils.RedisClient

@Singleton
class BacklogIssueService @Inject()(ws: WSClient, config: Configuration)(implicit ec: ExecutionContext) {
  private val backlogApiUrl = config.get[String]("backlog.api.url")
  private val redisKey = config.get[String]("backlog.redis.key")
  private val projectId = config.get[Int]("backlog.project.id")

  
def listIssues(): Future[JsResult[Seq[JsObject]]] = {
  val url = s"$backlogApiUrl/issues?projectId[]=$projectId"
  val token = RedisClient.get(redisKey)

  ws.url(url)
    .withHttpHeaders("Authorization" -> s"Bearer $token")
    .get()
    .map { response =>
      Json.parse(response.body).validate[Seq[JsObject]](
        Reads.seq(
          ((__ \ "id").read[Int] and
           (__ \ "issueType" \ "id").read[Int] and
           (__ \ "issueType" \ "name").read[String] and
           (__ \ "summary").read[String] and
           (__ \ "description").read[String] and
           (__ \ "startDate").readNullable[String] and
           (__ \ "dueDate").readNullable[String]
          )((id, typeId, typeName, summary, description, startDate, dueDate) => JsObject(Seq(
            "id" -> JsNumber(id),
            "issueTypeId" -> JsNumber(typeId),
            "issueTypeName" -> JsString(typeName),
            "summary" -> JsString(summary),
            "description" -> JsString(description),
            "startDate" -> startDate.fold[JsValue](JsNull)(JsString),
            "dueDate" -> dueDate.fold[JsValue](JsNull)(JsString)
          )))
        )
      )
    }
}

  // 创建问题
  def createIssue(summary: String, issueTypeId: Int, description: String, startDate: String, dueDate: String): Future[JsResult[JsObject]] = {
    val url = s"$backlogApiUrl/issues"
    val token = RedisClient.get(redisKey)

    val data = Map(
      "projectId" -> Seq(projectId.toString),
      "summary" -> Seq(summary),
      "issueTypeId" -> Seq(issueTypeId.toString),
      "priorityId" -> Seq("1"),
      "description" -> Seq(description),
      "startDate" -> Seq(startDate),
      "dueDate" -> Seq(dueDate)
    )

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

  def updateIssue(issueId: Int, summary: String, issueTypeId: Int, description: String, startDate: String, dueDate: String): Future[JsResult[JsObject]] = {
    val url = s"$backlogApiUrl/issues/$issueId"
    val token = RedisClient.get(redisKey)

    val data = Map(
      "summary" -> Seq(summary),
      "issueTypeId" -> Seq(issueTypeId.toString),
      "priorityId" -> Seq("1"),
      "description" -> Seq(description),
      "startDate" -> Seq(startDate),
      "dueDate" -> Seq(dueDate)
    )

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
