package models

import java.sql.Timestamp
import slick.jdbc.PostgresProfile.api._

case class User(id: Option[Long], username: String, password: String, role: Int, status: Option[Int])

class Users(tag: Tag) extends Table[User](tag, "user") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def password = column[String]("password")
  def role = column[Int]("role")
  def status = column[Option[Int]]("status")

  def * = (id.?, username, password, role, status) <> ((User.apply _).tupled, User.unapply)
}

