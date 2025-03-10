package models

import java.sql.Timestamp
import slick.jdbc.PostgresProfile.api._

case class Volunteer(id: Option[Long], name: String, email: String, phoneNumber: String, gender: Int, status: Option[Int])

class Volunteers(tag: Tag) extends Table[Volunteer](tag, "volunteer") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  def phoneNumber = column[String]("phone_number")
  def gender = column[Int]("gender")
  def status = column[Option[Int]]("status")

  def * = (id.?, name, email, phoneNumber, gender, status) <> ((Volunteer.apply _).tupled, Volunteer.unapply)
}
