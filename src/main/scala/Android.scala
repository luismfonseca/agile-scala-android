package agile.android

import sbt._
import scala.xml._

object Android
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

  def findPackageName(sourceDirectory: File) =
  {
    val manifestFile = findManifestPath(sourceDirectory)

    XML.loadFile(manifestFile).attribute("package").get.head.text
  }

  def getModelsPath(sourceDirectory: File, scalaSourceDirectory: File): File =
  {
    new File(scalaSourceDirectory.getPath + "/" + findPackageName(sourceDirectory).replace('.', '/') + "/models/")
  }
}