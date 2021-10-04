package controllers

import play.api.Logging
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, _}
import services.FilesService
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.Configuration
import play.api.libs.Files.TemporaryFile

case class FileForm(size: Int)

class FilesController @Inject()(cc: ControllerComponents, filesService: FilesService, config: Configuration)(implicit executionContext: ExecutionContext)
  extends AbstractController(cc) with Logging {

  def list(): Action[AnyContent] = {
    val files = filesService.list().mkString(", ")
    Action { Ok(files) }
  }

  def upload(name: String): Action[MultipartFormData[TemporaryFile]]  =
    Action(parse.multipartFormData) { implicit request =>
      request.body
        .file("upload_file") // key in the post form
        .map { uploadFile =>
          filesService.getPath(name).map { path =>
            logger.info(s"key = ${uploadFile.key}, file = ${name}, filename = ${uploadFile.filename}, " +
              s"contentType = ${uploadFile.contentType}, " +
              s"fileSize = ${uploadFile.fileSize}, dispositionType = ${uploadFile.dispositionType}")
            if (!filesService.doesFileExist(name)) {
              uploadFile.ref.copyTo(path, replace = true)
              Ok("File uploaded")
            }
            else
              UnprocessableEntity("file already exists")
          }.getOrElse(UnprocessableEntity(s"filename $name is invalid"))
        }.getOrElse {
          NotFound
        }
    }

  def delete(name: String): Action[AnyContent] =
    Action { implicit request =>
        if (filesService.delete(name))
          Ok
        else
          NotFound
    }
}
