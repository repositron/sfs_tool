package services

import play.api.{Configuration, Logging}
import java.nio.file.{Files, Path, Paths}
import javax.inject.{Inject, Singleton}

/**
  * This class provides service functionality for  the Files api.
 */
@Singleton
class FilesService @Inject() (config: Configuration) extends Logging {
  private val storagePath: Path = Paths.get(config.get[String]("sfs.storagePath"))

  logger.info(s"FilesService directoryPath = ${storagePath}")

  private def validateName(name: String): Boolean = {
    // check the file name has no directories or .. .
    val path = Paths.get(name)
    path == path.getFileName
  }

  def getPath(name: String): Option[Path]  = {
    logger.info("FilesService.getPath")
    if (validateName(name))
      Some(storagePath.resolve(name))
    else
      None
  }

  def doesFileExist(name: String): Boolean = {
    getPath(name).map(_.toFile.exists()).getOrElse(false)
  }

  def delete(name: String): Boolean = {
    logger.info("FilesService.delete")
    if (validateName(name)) {
      val path = storagePath.resolve(name)
      path.toFile.delete()
    }
    else
      false
  }

  def list(): List[String] = {
    logger.info("FilesService.list")
    os.list(os.Path(storagePath)).filter(os.isFile(_)).map(_.last).toList
  }
}
