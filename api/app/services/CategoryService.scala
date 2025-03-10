package services

import javax.inject._
import repositories.CategoryRepository
import models.Category
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryService @Inject()(categoryRepository: CategoryRepository)
                               (implicit ec: ExecutionContext) {

  /**
   * Adds a new category.
   * @param name The name of the category to be added.
   * @return A Future[Int] that represents the number of rows affected, typically 1 if the insert is successful.
   */
  def addCategory(name: String): Future[Int] = {
    categoryRepository.insert(name)
  }

  /**
   * Retrieves all categories from the database.
   * @return A Future[Seq[Category]] that contains a sequence of all categories currently stored.
   */
  def getAllCategories(): Future[Seq[Category]] = {
    categoryRepository.getAll()
  }

  /**
   * Updates the name of an existing category.
   * @param id The ID of the category to update.
   * @param name The new name for the category.
   * @return A Future[Int] that indicates the number of rows affected by the update.
   */
  def updateCategory(id: Int, name: String): Future[Int] = {
    categoryRepository.update(id, name)
  }
}
