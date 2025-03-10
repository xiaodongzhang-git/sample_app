
package repositories

import javax.inject._
import models.{Issue, Issues}
import slick.jdbc.JdbcProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IssueRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                               (implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val issues = TableQuery[Issues]

  // Create a new issue
  def createIssue(issueId: Long, wikiContent: String, volunteerIds: String): Future[Int] = {
    val newIssue = Issue(issueId, wikiContent, volunteerIds)
    db.run(issues += newIssue).map(_ => 1)
  }

  // Get all issues
  def listIssues(): Future[Seq[Issue]] = {
    db.run(issues.result)
  }

  // Update an issue
  def updateIssue(issueId: Long, wikiContent: String, volunteerIds: String): Future[Int] = {
    val updateAction = issues.filter(_.issueId === issueId)
                             .map(issue => (issue.wikiContent, issue.volunteerIds))
                             .update((wikiContent, volunteerIds))
    db.run(updateAction)
  }

  // Find issue by ID
  def findIssueById(issueId: Long): Future[Option[Issue]] = {
    db.run(issues.filter(_.issueId === issueId).result.headOption)
  }
}
