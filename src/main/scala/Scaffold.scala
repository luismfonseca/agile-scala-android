import sbt._
import sbt.complete._
import java.io._
import java.net._
import scala.xml._
import java.lang.ClassLoader

object Scaffold
{

	def findClassesPath(baseDirectory: File): File = new File((baseDirectory / "target" ** "classes").getPaths(0))

	def scaffoldFromModel(baseDirectory: File, sourceDirectory: File, scalaSourceDirectory: File, modelName: String) =
	{
		val classesPath = findClassesPath(baseDirectory)
		val packageName = Android.findPackageName(sourceDirectory)
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
		val packageName = Android.findPackageName(sourceDirectory)
		val modelsPath = new File(classesPath.toString + "/" + packageName.replace('.', '\\') + "/models/")

		val classLoader = new URLClassLoader(Array[URL](classesPath.toURL))

		val models = modelsPath.listFiles().map(modelPath => {
			modelPath.getName().split('.').head
		})

		sbt.complete.Parsers.spaceDelimited(models mkString " ")
	}

}