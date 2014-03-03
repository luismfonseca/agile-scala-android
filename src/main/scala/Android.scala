import sbt._
import sbt.complete._
import java.io._
import scala.xml._

object Android
{
	def findManifestPath(sourceDirectory: File): File = new File((sourceDirectory ** "AndroidManifest.xml").getPaths(0))

	def findPackageName(sourceDirectory: File) =
	{
        val manifestFile = findManifestPath(sourceDirectory)

        XML.loadFile(manifestFile).attribute("package").get.head.text
    }

    def getModelsPath(sourceDirectory: File, scalaSourceDirectory: File): File =
    {
    	new File(scalaSourceDirectory.getPath + "/" + findPackageName(sourceDirectory).replace('.', '\\') + "/models/")
    }
}