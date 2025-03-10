package services

import javax.inject._
import repositories.UserRepository
import models.User
import utils.JwtUtils
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthService @Inject()(userRepository: UserRepository)
                           (implicit ec: ExecutionContext) {

  /** User login */
  def login(username: String, password: String): Future[Option[String]] = {
    userRepository.validateUser(username, password).map {
      case Some(user) if user.status.contains(1) =>  // check status
        Some(JwtUtils.generateToken(user.username, user.role.toString))
      case _ =>
        None
    }
  }
}
