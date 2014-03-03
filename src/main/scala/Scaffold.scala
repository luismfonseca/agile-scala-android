import sbt._
import sbt.complete._
import java.io._
import java.net._
import scala.xml._
import java.lang.ClassLoader

object Scaffold
{

	def scaffoldFromModel(classDirectory: File, sourceDirectory: File, scalaSourceDirectory: File, modelName: String) =
	{
		val packageName = Android.findPackageName(sourceDirectory)
		val modelsPath = new File(classDirectory.toString + "/" + packageName.replace('.', '\\') + "/models/")

		val classLoader = new URLClassLoader(Array[URL](classDirectory.toURL))

		if (modelsPath.listFiles() == null)
		{
			throw new Exception("Models folder not found.")
		}

		if (modelsPath.listFiles().exists(_.getName() == modelName + ".class") == false)
		{
			throw new Exception("Model '" + modelName + "' not found.")
		}
		
		val modelClass = classLoader.loadClass(packageName + ".models." + modelName)

		val modelClassFields = modelClass.getDeclaredFields()

		IO.write(new File(scalaSourceDirectory.getPath() + "extracted.txt"), modelClassFields.deep.mkString("\n"))
		//TODO: use this knowledge to scaffold activities and layouts.
	}

	def findModels(classDirectory: File, sourceDirectory: File): Parser[Seq[String]] =
	{
		val packageName = Android.findPackageName(sourceDirectory)
		val modelsPath = new File(classDirectory.toString + "/" + packageName.replace('.', '\\') + "/models/")

		val classLoader = new URLClassLoader(Array[URL](classDirectory.toURL))

		if (modelsPath.listFiles() == null)
		{
		  sbt.complete.Parsers.spaceDelimited(" modelName")
		}
		else
		{
	      val models = modelsPath.listFiles().map(modelPath => {
	  	    modelPath.getName().split('.').head
		  })

		  sbt.complete.Parsers.spaceDelimited(models mkString " ")
		}
	}

}