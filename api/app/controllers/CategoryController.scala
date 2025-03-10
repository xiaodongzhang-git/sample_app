package controllers

import javax.inject._
import services.CategoryService
import dto.{ApiResponse, CategoryRequest}
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import utils.JwtAuthAction

@Singleton
class CategoryController @Inject()(categoryService: CategoryService,
                                   val controllerComponents: ControllerComponents,
                                   jwtAuthAction: JwtAuthAction)
                                  (implicit ec: ExecutionContext)
  extends BaseController {

  implicit val apiResponseWrites: OWrites[ApiResponse[JsObject]] = Json.writes[ApiResponse[JsObject]]
  implicit val categoryListWrites: OWrites[ApiResponse[Seq[JsObject]]] = Json.writes[ApiResponse[Seq[JsObject]]]

  /** （POST /categories） - admin only */
  def addCategory = jwtAuthAction.async(parse.json) { request =>
    val role = request.attrs.get(JwtAuthAction.UserRole).getOrElse("2")

    if (role == "2") {
      Future.successful(Forbidden(Json.toJson(ApiResponse.error(403, "Insufficient permissions, the operator cannot add categories"))))
    } else {
      request.body.validate[CategoryRequest].map { categoryRequest =>
        categoryService.addCategory(categoryRequest.name).map { id =>
          Ok(Json.toJson(ApiResponse.success(Json.obj("id" -> id))))
        }
      }.getOrElse(Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, "Invalid JSON")))))
    }
  }

  /** （GET /categories） - all */
  def getCategories = jwtAuthAction.async { request =>
  categoryService.getAllCategories().map { categories =>
    val categoryJson = categories.map { category =>
      Json.obj(
        "id" -> category.id,
        "name" -> category.name
      )
    }
    Ok(Json.toJson(ApiResponse.success(categoryJson)))
  }
}


  /** （PUT /categories/:id） - admin only */
  def updateCategory(id: Int) = jwtAuthAction.async(parse.json) { request =>
    val role = request.attrs.get(JwtAuthAction.UserRole).getOrElse("2")

    if (role == "2") {
      Future.successful(Forbidden(Json.toJson(ApiResponse.error(403, "Insufficient permissions, the operator cannot edit categories"))))
    } else {
      request.body.validate[CategoryRequest].map { categoryRequest =>
        categoryService.updateCategory(id, categoryRequest.name).map { rowsUpdated =>
          if (rowsUpdated > 0)
            Ok(Json.toJson(ApiResponse.success(Json.obj("updated" -> true))))
          else
            NotFound(Json.toJson(ApiResponse.error(404, "categories no found")))
        }
      }.getOrElse(Future.successful(BadRequest(Json.toJson(ApiResponse.error(400, "Invalid JSON")))))
    }
  }
}
