package repositories

import javax.inject._
import models.{Volunteer, Volunteers}
import slick.jdbc.JdbcProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VolunteerRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                                   (implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val volunteers = TableQuery[Volunteers]

  /**
   * Create a new volunteer with basic information.
   * @param name Name of the volunteer.
   * @param email Email of the volunteer.
   * @param phoneNumber Phone number of the volunteer.
   * @param gender Gender of the volunteer.
   * @return A Future[Int] indicating the result of the insert operation.
   */
  def createVolunteer(name: String, email: String, phoneNumber: String, gender: Int): Future[Int] = {
    val newVolunteer = Volunteer(None, name, email, phoneNumber, gender, Some(1))
    db.run(volunteers += newVolunteer).map(_ => 1)
  }

  /**
   * List all volunteers.
   * @return A Future[Seq[Volunteer]] of all registered volunteers.
   */
  def listVolunteers(): Future[Seq[Volunteer]] = {
    db.run(volunteers.result)
  }

  /**
   * Update volunteer information.
   * @param id ID of the volunteer to update.
   * @param name Optional updated name.
   * @param email Optional updated email.
   * @param phoneNumber Optional updated phone number.
   * @param gender Optional updated gender.
   * @param status Optional updated status.
   * @return A Future[Int] indicating the number of rows updated.
   */
  def updateVolunteer(id: Long, name: Option[String], email: Option[String], phoneNumber: Option[String], gender: Option[Int], status: Option[Int]): Future[Int] = {
    val updateActions = volunteers.filter(_.id === id).result.head.flatMap { volunteer =>
      val updatedVolunteer = Volunteer(
        id = Some(id),
        name = name.getOrElse(volunteer.name),
        email = email.getOrElse(volunteer.email),
        phoneNumber = phoneNumber.getOrElse(volunteer.phoneNumber),
        gender = gender.getOrElse(volunteer.gender),
        status = status.orElse(volunteer.status).orElse(Some(1))
      )
      volunteers.filter(_.id === id).update(updatedVolunteer)
    }
    db.run(updateActions)
  }
}
