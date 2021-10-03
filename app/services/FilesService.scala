package services

import play.api.{Configuration, Logging}
import java.nio.file.{Files, Path, Paths}
import javax.inject.{Inject, Singleton}

/**
  * This class provides functions the Files api
 */
@Singleton
class FilesService @Inject() (config: Configuration) extends Logging {
  private val directoryPath: Path = Paths.get(config.get[String]("sfs.storagePath"))

  def validateName(name: String): Boolean = {
    // check the file name has no directories or .. .
    val path = Paths.get(name)
    path == path.getFileName
  }

  def getPath(name: String): Option[Path]  = {
    logger.info("FilesService.getPath")
    if (validateName(name))
      Some(directoryPath.resolve(name))
    else
      None
  }

  def delete(name: String): Boolean = {
    logger.info("FilesService.delete")
    if (validateName(name)) {
      val path = directoryPath.resolve(name)
      path.toFile.delete()
    }
    else
      false
  }

  def list(): List[String] = {
    logger.info("FilesService.list")
    os.list(os.Path(directoryPath)).filter(os.isFile(_)).map(_.last).toList
  }
}
