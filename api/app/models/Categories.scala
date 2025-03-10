package models

import slick.jdbc.MySQLProfile.api._
import java.sql.Timestamp

case class Category(id: Option[Int], name: String)

class Categories(tag: Tag) extends Table[Category](tag, "categories") {
  def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def * = (id, name) <> (Category.tupled, Category.unapply)
}

object Categories {
  val table = TableQuery[Categories]
}
