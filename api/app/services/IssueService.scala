package services

import javax.inject._
import repositories.IssueRepository
import models.Issue
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IssueService @Inject()(issueRepository: IssueRepository)(implicit ec: ExecutionContext) {

  /**
   * Create a new issue.
   * @param issueId The ID of the issue, not auto-generated.
   * @param wikiContent The wiki ID associated with the issue.
   * @param volunteerIds A comma-separated string of volunteer IDs.
   * @return A Future[Int] that indicates the result of the insert operation, typically the number of rows inserted.
   */
  def createIssue(issueId: Long, wikiContent: String, volunteerIds: String): Future[Int] = {
    issueRepository.createIssue(issueId, wikiContent, volunteerIds)
  }

  /**
   * Retrieve a list of all issues.
   * @return A Future[Seq[Issue]] containing all issues.
   */
  def listIssues(): Future[Seq[Issue]] = {
    issueRepository.listIssues()
  }

  /**
   * Update an issue information.
   * @param issueId The issue ID.
   * @param wikiContent Updated wiki ID.
   * @param volunteerIds Updated list of volunteer IDs.
   * @return A Future[Int] indicating the result of the update operation, number of rows affected.
   */
  def updateIssue(issueId: Long, wikiContent: String, volunteerIds: String): Future[Int] = {
    issueRepository.updateIssue(issueId, wikiContent, volunteerIds)
  }

  /**
   * Find an issue by its ID.
   * @param issueId The ID of the issue to find.
   * @return A Future[Option[Issue]] containing the found issue, if any.
   */
  def findIssueById(issueId: Long): Future[Option[Issue]] = {
    issueRepository.findIssueById(issueId)
  }
}
