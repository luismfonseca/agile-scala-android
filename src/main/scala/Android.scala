package agile.android

import sbt._
import scala.xml._

protected object Android
{
  def findManifestPath(sourceDirectory: File): File =
  {
    try
    {
      new File((sourceDirectory ** "AndroidManifest.xml").getPaths(0))
    }
    catch
    {
      case _ : Throwable => throw new Exception("""Manifest file was not found!
                                                  |Is this an Android project?""".stripMargin)
    }
  }

  def findPackageName(sourceDirectory: File): String =
  {
    val manifestFile = findManifestPath(sourceDirectory)

    XML.loadFile(manifestFile).attribute("package").get.head.text
  }

  def getModelsPath(sourceDirectory: File): File =
  {
    new File(sourceDirectory.getPath + "/main/scala/" + findPackageName(sourceDirectory).replace('.', '/') + "/models/")
  }

  def getModels(sourceDirectory: File): Seq[String] =
  {
    val modelsPath = getModelsPath(sourceDirectory)

    if (modelsPath.isDirectory == false)
    {
      Nil
    }
    else
    {
      modelsPath.listFiles().map(_.getName().split('.').head)
    }
  }

  def getManifestPermissions(sourceDirectory: File): Seq[String] =
  {
    val manifestFile = findManifestPath(sourceDirectory)

    val pemissionsXML = XML.loadFile(manifestFile).child.filter(_.label == "permission")

    pemissionsXML.map(_.attribute("http://schemas.android.com/apk/res/android", "name").getOrElse(new Text("")).head.text)
  }

  def manifestAddPermissions(sourceDirectory: File, missingPermissions: Array[String]): Unit = {

    val manifestFile = findManifestPath(sourceDirectory)

    val manifest = XML.loadFile(manifestFile)

    val missingPermissionsXML = missingPermissions.map(permission => <uses-permission android:name={permission}></uses-permission>)

    val newManifest = manifest.copy(child = missingPermissionsXML ++ manifest.child)

    IO.write(manifestFile, newManifest.toString)
  }
}