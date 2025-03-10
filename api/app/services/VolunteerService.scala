package services

import javax.inject._
import repositories.VolunteerRepository
import models.Volunteer
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VolunteerService @Inject()(volunteerRepository: VolunteerRepository)
                                (implicit ec: ExecutionContext) {

  /**
   * Create a new volunteer.
   * @param name The name of the volunteer.
   * @param email The email of the volunteer.
   * @param phoneNumber The phone number of the volunteer.
   * @param gender The gender of the volunteer.
   * @return A Future[Int] that indicates the result of the insert operation, typically the number of rows inserted.
   */
  def createVolunteer(name: String, email: String, phoneNumber: String, gender: Int): Future[Int] = {
    volunteerRepository.createVolunteer(name, email, phoneNumber, gender)
  }

  /**
   * Retrieve a list of all volunteers.
   * @return A Future[Seq[Volunteer]] containing all volunteers.
   */
  def getAllVolunteers(): Future[Seq[Volunteer]] = {
    volunteerRepository.listVolunteers()
  }

  /**
   * Update volunteer information.
   * @param id The volunteer ID.
   * @param name Optional new name.
   * @param email Optional new email.
   * @param phoneNumber Optional new phone number.
   * @param gender Optional new gender.
   * @param status Optional new status.
   * @return A Future[Int] indicating the result of the update operation.
   */
  def updateVolunteer(id: Int, name: Option[String], email: Option[String], phoneNumber: Option[String], gender: Option[Int], status: Option[Int]): Future[Int] = {
    volunteerRepository.updateVolunteer(id.toLong, name, email, phoneNumber, gender, status)
  }
}
