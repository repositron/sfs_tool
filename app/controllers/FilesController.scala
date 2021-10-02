package controllers

import play.api.Logging
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.Files.TemporaryFile
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, _}
import play.api.mvc.MultipartFormData.FilePart
import play.core.parsers.Multipart.{FileInfo, FilePartHandler}
import services.FilesService
import akka.stream.IOResult
import akka.stream.scaladsl._
import akka.util.ByteString
import play.api.libs.streams.Accumulator

import java.io.File
import java.nio.file.{Files, Path}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

case class FileForm(size: Int)

class FilesController @Inject()(cc: ControllerComponents, filesService: FilesService)(implicit executionContext: ExecutionContext)
  extends AbstractController(cc) with Logging {

  private def handleFilePartAsFile: FilePartHandler[File] = {
    case FileInfo(partName, filename, contentType, _) =>

      val path: Path = Files.createTempFile("multipartBody", "tempFile")
      val fileSink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(path)
      val accumulator: Accumulator[ByteString, IOResult] = Accumulator(fileSink)
      accumulator.map {
        //case IOResult(_, Failure(error)) => Future.failed(error)
        case IOResult(count, status) =>
          logger.info(s"count = $count, status = $status")
          FilePart(partName, filename, contentType, path.toFile)
      }
  }

  def list(): Action[AnyContent] = {
    Action { Ok }
  }

  def upload(name: String): Action[MultipartFormData[TemporaryFile]]  = Action(parse.multipartFormData) {
    request =>
        request.body
          .file("upload_file") // key in the post form
          .map { uploadFile =>
            val filename = uploadFile.filename
            val fileSize = uploadFile.fileSize
            val contentType = uploadFile.contentType
            logger.info(s"key = ${uploadFile.key}, filename = ${filename}, " +
              s"contentType = ${uploadFile.contentType}, " +
              s"fileSize = ${uploadFile.fileSize}, dispositionType = ${uploadFile.dispositionType}")

           // uploadFile.ref.copyTo(Paths.get(s"/tmp/picture/$filename"), replace = true)
            filesService
            Ok("File uploaded")
          }
          .getOrElse {
            NotFound
          }
      }


  def delete(name: String): Action[AnyContent] = {
    Action { Ok }
  }


}
