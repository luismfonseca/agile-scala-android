import java.io.File
import collection.immutable.ListMap

object Create
{

  def templateKeys(sbtVersion: String, packageName: String, minSdkVersion: Int) = {
    ListMap[String, String](
      "SBT_VERSION" -> sbtVersion,
      "PACKAGE_NAME" -> packageName,
      "MIN_SDK_VERSION" -> minSdkVersion.toString
    )
  }

  def applyTemplate(templateKeysNewProject: ListMap[String, String], templateString: String) = {
    templateKeysNewProject.foldLeft(templateString) {
      (resultingMenu, currentMapEntry) =>
        resultingMenu.replace(currentMapEntry._1, currentMapEntry._2)
    }
  }

  val sbtBuildPropertiesFile = new File("project/build.properties")
  val sbtBuildFile = new File("build.sbt")
  val androidManifestFile = new File("src/main/AndroidManifest.xml")
  val valuesStringFile = new File("src/main/res/values/string.xml")
  val layoutMainFile = new File("src/main/res/layout/main.xml")
  val drawableHdpiFile = new File("src/main/res/drawable-hdpi/ic_launcher.png")
  val drawableMdpiFile = new File("src/main/res/drawable-mdpi/ic_launcher.png")
  val drawableXHdpiFile = new File("src/main/res/drawable-xhdpi/ic_launcher.png")
  val gitignoreFile = new File(".gitignore")

  
  def directoriesWith(packageName: String, minSdkVersion: Int) = {
    
      val commonDirectories = Seq[String](
        "src/main/res/values",
        "src/main/res/layout",
        "src/main/res/menu",
        "src/test/scala",
        "src/main/scala/" + packageName.replace('.', '/'),
        "src/main/scala/" + packageName.replace('.', '/') + "/models",
        "src/main/scala/" + packageName.replace('.', '/') + "/ui"
      )

      val allDirectories =
        if (minSdkVersion < 14)
          commonDirectories
        else
          commonDirectories ++ Seq[String](
            "src/main/res/drawable-hdpi",
            "src/main/res/drawable-mdpi",
            "src/main/res/drawable-xhdpi"
          )

      allDirectories map(new File(_))
  }

  def sbtBuildPropertiesContent =
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("create/project/build.properties"))

  def sbtBuildContent =
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("create/build.sbt"))

  def manifestXMLContent =
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("create/src/main/AndroidManifest.xml"))

  def valuesStringXMLContent =
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("create/res/values/string.xml"))

  def layoutMainXMLContent =
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("create/res/layout/main.xml"))

  def defaultGitIgnoreContent =
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("create/gitignore"))

  def drawableHdpiByteArray =
    Util.convertInputStreamToArray(getClass.getClassLoader().getResourceAsStream("create/res/drawable-hdpi/ic_launcher.png"))

  def drawableMdpiByteArray =
    Util.convertInputStreamToArray(getClass.getClassLoader().getResourceAsStream("create/res/drawable-mdpi/ic_launcher.png"))

  def drawableXHdpiByteArray =
    Util.convertInputStreamToArray(getClass.getClassLoader().getResourceAsStream("create/res/drawable-xhdpi/ic_launcher.png"))
}