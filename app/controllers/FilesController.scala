package controllers

import play.api.Logging
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, _}
import play.api.mvc.MultipartFormData.{BadPart, FilePart, ParseError}
import play.core.parsers.Multipart.{FileInfo, FilePartHandler}
import services.FilesService
import akka.stream.IOResult
import akka.stream.scaladsl._
import akka.util.ByteString
import play.api.libs.streams.Accumulator

import java.io.File
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import play.api.Configuration
import play.api.libs.Files.TemporaryFile

case class FileForm(size: Int)

class FilesController @Inject()(cc: ControllerComponents, filesService: FilesService, config: Configuration)(implicit executionContext: ExecutionContext)
  extends AbstractController(cc) with Logging {
   private def handleFilePartAsFile: FilePartHandler[File] = {
    case FileInfo(partName, filename, contentType, _) =>
      val storePath = filesService.getPath(filename).get
      val fileSink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(storePath)
      val accumulator: Accumulator[ByteString, IOResult] = Accumulator(fileSink)
      accumulator.map {
        case IOResult(count, status) =>
          logger.info(s"count = $count, status = $status")
          FilePart(partName, filename, contentType, storePath.toFile)
      }
  }

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
              uploadFile.ref.copyTo(path, replace = true)
              Ok("File uploaded")
            }.getOrElse(NotFound)
          }
          .getOrElse {
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
