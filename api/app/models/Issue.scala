package models

import java.sql.Timestamp
import slick.jdbc.PostgresProfile.api._

case class Issue(issueId: Long, wikiContent: String, volunteerIds: String)

class Issues(tag: Tag) extends Table[Issue](tag, "issue") {
  def issueId = column[Long]("issue_id", O.PrimaryKey)
  def wikiContent = column[String]("wiki_content")
  def volunteerIds = column[String]("volunteer_ids")

  def * = (issueId, wikiContent, volunteerIds) <> ((Issue.apply _).tupled, Issue.unapply)
}

