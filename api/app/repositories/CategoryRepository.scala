package repositories

import javax.inject._
import models.{Category, Categories}
import slick.jdbc.JdbcProfile
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                             (implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val categories = TableQuery[Categories]

  /** create category */
  def insert(name: String): Future[Int] = {
    val action = (categories returning categories.map(_.id)) += Category(None, name)
    db.run(action).map(_.getOrElse(0))
  }


  /** get all categories */
  def getAll(): Future[Seq[Category]] = {
    db.run(categories.result)
  }

  /** update category */
  def update(id: Int, name: String): Future[Int] = {
    val action = categories.filter(_.id === id).map(_.name).update(name)
    db.run(action)
  }
}
