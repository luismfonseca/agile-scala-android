package agile.android

import sbt.IO
import java.io.File
import collection.immutable.ListMap

object Create
{

  def templateKeys(sbtVersion: String, pluginVersion: String, packageName: String, minSdkVersion: Int) = {
    ListMap[String, String](
      "SBT_VERSION" -> sbtVersion,
      "PLUGIN_VERSION" -> "0.6",
      "PACKAGE_NAME_AS_DIR" -> packageName.replace('.', '/'),
      "PACKAGE_NAME" -> packageName,
      "PACKAGE_MODELS" -> (packageName + ".models"),
      "PACKAGE_DB" -> (packageName + ".db"),
      "MIN_SDK_VERSION" -> minSdkVersion.toString,
      "GITIGNORE_FILE_NAME" -> ".gitignore"
    )
  }

  def applyTemplate(templateKeysNewProject: ListMap[String, String], templateString: String) = {
    templateKeysNewProject.foldLeft(templateString) {
      (resultingMenu, currentMapEntry) =>
        resultingMenu.replace(currentMapEntry._1, currentMapEntry._2)
    }
  }

  def newProjectAndroid(sbtLogger: sbt.Logger, sbtVersion: String, pluginVersion: String, packageName: String, minSdkVersion: Int): Unit = {

    if (packageName.matches("""([\p{L}_$][\p{L}\p{N}_$]*\.)*[\p{L}_$][\p{L}\p{N}_$]*""") == false) {
      throw new Exception("Given package name is not valid.")
    }

    val templateKeysNewProject = templateKeys(sbtVersion, pluginVersion, packageName, minSdkVersion)

    Util.getResourceFiles("create/").foreach {
      case (filePath, fileContent) => {
        val finalFilePath = new File(applyTemplate(templateKeysNewProject, filePath))

        val finalFileContent =  applyTemplate(templateKeysNewProject, fileContent)

        // TODO: enforce override\merge policies here.
        if (finalFilePath.exists == false)
        {
          IO.write(finalFilePath, finalFileContent)
        }
      }
    }

    Util.getResourceFilesRaw("create-raw/").foreach {
      case (filePath, finalFileContent) => {
        val finalFilePath = new File(applyTemplate(templateKeysNewProject, filePath))

        // TODO: enforce override\merge policies here.
        if (finalFilePath.exists == false)
        {
          IO.write(finalFilePath, finalFileContent)
        }
      }
    }

    // create missing folders
    IO.createDirectories(
      Seq("src/main/scala/PACKAGE_NAME_AS_DIR/models/")
        map(path => new File(applyTemplate(templateKeysNewProject, path)))
    )

    sbtLogger.info("Generated sbt build definitions and the needed Android source files.")
    sbtLogger.info("Generated a .gitignore file.")

    sbtLogger.warn("Please type 'reload' to allow sbt to load the new build definitions!")
  }
}