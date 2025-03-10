package services

import javax.inject._
import repositories.UserRepository
import models.User
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject()(userRepository: UserRepository)
                           (implicit ec: ExecutionContext) {

  /**
   * Create a new user.
   * @param username The username of the user.
   * @param password The password of the user.
   * @param role The role of the user.
   * @return A Future[Int] that indicates the result of the insert operation, typically the number of rows inserted.
   */
  def createUser(username: String, password: String, role: Int): Future[Int] = {
    userRepository.createUser(username, password, role)
  }

  /**
   * Retrieve a list of all users.
   * @return A Future[Seq[User]] containing all users.
   */
  def getAllUsers(): Future[Seq[User]] = {
    userRepository.listUsers()
  }

  /**
   * Update user information.
   * @param id The user ID.
   * @param username Optional new username.
   * @param password Optional new password.
   * @param role Optional new role.
   * @return A Future[Int] indicating the result of the update operation.
   */
  def updateUser(id: Int, username: Option[String], password: Option[String], role: Option[Int], status: Option[Int]): Future[Int] = {
    userRepository.updateUser(id.toLong, username, password, role, status)
  }

}
