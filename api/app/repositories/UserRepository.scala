package repositories

import javax.inject._
import models.{User, Users}
import slick.jdbc.JdbcProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}
import org.mindrot.jbcrypt.BCrypt

@Singleton
class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                              (implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val users = TableQuery[Users]

  // Helper method: Hashes the password
  private def hashPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

  // Create a new user with hashed password
  def createUser(username: String, password: String, role: Int): Future[Int] = {
    val hashedPassword = hashPassword(password)
    val newUser = User(None, username, hashedPassword, role, Some(1))
    db.run(users += newUser).map(_ => 1)
  }

  // Find user by username and password with status check
  def validateUser(username: String, password: String): Future[Option[User]] = {
    db.run(users.filter(user => user.username === username).result.headOption).flatMap {
      case Some(user) if BCrypt.checkpw(password, user.password) => Future.successful(Some(user))
      case _ => Future.successful(None)
    }
  }

  // List all users
  def listUsers(): Future[Seq[User]] = {
    db.run(users.result)
  }

  // Update user information including status
  def updateUser(id: Long, username: Option[String], password: Option[String], role: Option[Int], status: Option[Int]): Future[Int] = {

    val updateActions = users.filter(_.id === id).result.head.flatMap { user =>
      val updatedUser = User(
        id = Some(id),
        username = username.getOrElse(user.username),
        password = password.map(hashPassword).getOrElse(user.password),
        role = role.getOrElse(user.role),
        status.orElse(user.status).orElse(Some(1))
      )
      users.filter(_.id === id).update(updatedUser)
    }
    db.run(updateActions)
  }

}
