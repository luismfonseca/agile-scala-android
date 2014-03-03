import sbt._
import java.io._
import java.net._
import scala.xml._
import java.lang.ClassLoader
import sbt.complete._

object Scaffold {


	def findPackage(baseDirectory: File, sourceDirectory: File) =
	{
        val manifestFile = findManifestPath(sourceDirectory)

        XML.loadFile(manifestFile).attribute("package").get.head.text
    }

	def findClassesPath(baseDirectory: File): File = new File((baseDirectory / "target" ** "classes").getPaths(0))

	def findManifestPath(sourceDirectory: File): File = new File((sourceDirectory ** "AndroidManifest.xml").getPaths(0))

	def scaffoldModel(baseDirectory: File, sourceDirectory: File, scalaSourceDirectory: File, modelName: String) =
	{
		val classesPath = findClassesPath(baseDirectory)
		val packageName = findPackage(baseDirectory, sourceDirectory)
		val modelsPath = new File(classesPath.toString + "/" + packageName.replace('.', '\\') + "/models/")

		val classLoader = new URLClassLoader(Array[URL](classesPath.toURL))

		if (modelsPath.listFiles().exists(_.getName() == modelName + ".class") == false)
		{
			throw new Exception("Model '" + modelName + "' not found.")
		}
		
		val modelClass = classLoader.loadClass(packageName + ".models." + modelName)

		val modelClassFields = modelClass.getDeclaredFields()

		IO.write(new File(scalaSourceDirectory.getPath() + "extracted.txt"), modelClassFields.deep.mkString("\n"))
		//TODO: use this knowledge to scaffold activities and layouts.
	}

	def findModels(baseDirectory: File, sourceDirectory: File): Parser[Seq[String]] =
	{

		val classesPath = findClassesPath(baseDirectory)
		val packageName = findPackage(baseDirectory, sourceDirectory)
		val modelsPath = new File(classesPath.toString + "/" + packageName.replace('.', '\\') + "/models/")

		val classLoader = new URLClassLoader(Array[URL](classesPath.toURL))

		val models = modelsPath.listFiles().map(modelPath => {
			modelPath.getName().split('.').head
		})

		sbt.complete.Parsers.spaceDelimited(models mkString " ")
	}

}