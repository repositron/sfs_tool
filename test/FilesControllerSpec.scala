import play.api.test.Injecting

import java.io._
import java.nio.file.Files

import akka.stream.scaladsl._
import akka.util.ByteString
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._

class FilesControllerSpec extends PlaySpec with GuiceOneServerPerSuite with Injecting {
  val baseUrl = s"http://localhost:${port}/files"
  "FileController" must {
    "upload a file successfully" in {
      val response = uploadFile("testFile")
      response mustBe OK
    }
  }

  def deleteFile(name: String) = {
    val url = baseUrl + "/" + name
    val responseFuture = inject[WSClient].url(url).delete()
    await(responseFuture)
  }

  def uploadFile(name: String) = {
    val tmpFile = java.io.File.createTempFile("prefix", "txt")
    tmpFile.deleteOnExit()
    val msg = "hello world"
    Files.write(tmpFile.toPath, msg.getBytes())
    val name = "testfile"
    val url = baseUrl + "/" + name
    val responseFuture = inject[WSClient].url(url).post(postSource(tmpFile, name))
    await(responseFuture)
  }

  def postSource(tmpFile: File, name: String): Source[MultipartFormData.Part[Source[ByteString, _]], _] = {
    import play.api.mvc.MultipartFormData._
    Source(FilePart("upload_file", name, Option("text/plain"),
      FileIO.fromPath(tmpFile.toPath)) :: DataPart("key", "value") :: List())
  }
}
