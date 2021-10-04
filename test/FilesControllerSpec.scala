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
import org.scalatest.matchers.should.Matchers._

class FilesControllerSpec extends PlaySpec with GuiceOneServerPerSuite with Injecting {
  val baseUrl = s"http://localhost:${port}/files"
  "FileController" must {
    "upload a file successfully" in {
      cleanStorageDirectory()
      val response = uploadFile("testFile")
      response.status mustBe OK
      deleteFile("testFile")
    }
  }

  "FileController" must {
    "delete a file successfully" in {
      cleanStorageDirectory()
      val response = uploadFile("testFileforDelete")
      response.status mustBe OK

      val deleteResponse = deleteFile("testFileforDelete")
      deleteResponse.status mustBe OK
    }
  }

  "FileController" must {
    "list all files uploaded" in {
      cleanStorageDirectory()
      uploadFile("testFileforList1").status mustBe OK

      uploadFile("testFileforList2").status mustBe OK

      val listResponse = await(inject[WSClient].url(baseUrl).get())

      listResponse.status mustBe OK
      val listedFiles = listResponse.body.split(",").map(_.trim).toSet
      (listedFiles should contain ("testFileforList1"))
      (listedFiles should contain ("testFileforList2"))

    }
  }

  "FileController" must {
    "return an error if the file is already uploaded" in {
      cleanStorageDirectory()
      uploadFile("testFileforList1").status mustBe OK

      uploadFile("testFileforList1").status mustBe UNPROCESSABLE_ENTITY

    }
  }

  "FileController" must {
    "return an error if the file name is a relative path" in {
      cleanStorageDirectory()
      val response = uploadFile("../abcd")
      println(response.body)
      // this transaction is actually prevented by the routing table.
      response.status must not be (OK)
    }
  }

  def cleanStorageDirectory(): Unit = {
    val listResponse = await(inject[WSClient].url(baseUrl).get())
    listResponse.body.split(",").map(_.trim).foreach(deleteFile(_))
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
