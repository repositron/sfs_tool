package services

import play.api.Logging
import play.api.libs.Files.TemporaryFile

class FilesService(storeDirectory: os.Path) extends Logging {

  def validateName(name: String): Boolean = {
    // check the file name has no directories or .. .
    val path = os.Path(name)
    path == path.last
  }

  def getPath(name: String): Option[os.Path]  = {
    if (validateName(name))
      Some(storeDirectory/name)
    else
      None
  }

  def delete(fileName: String): Boolean = {
    false
  }

  def list(): List[String] = {
    List()
  }
}
