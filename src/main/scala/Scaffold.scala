import sbt._
import sbt.complete._
import java.io._
import java.net._
import scala.xml._
import java.lang.ClassLoader
import java.lang.reflect.Field

object Scaffold
{

  private def templateKeys(packageName: String, modelName: String, modelFields: Array[Field]) = {
    Map[String, String](
      "MENU_CONTEXT" -> menuContext(packageName, modelName),
      "MENU_ID" -> "@+id/menu_main_CLASS_NAME_UNDERSCORED",
      "MENU_TITLE" -> "@string/menu_main_new_CLASS_NAME_UNDERSCORED",
      "ID_EDIT_ACTIVITY" -> "@+id/edit_CLASS_NAME_UNDERSCORED_container",
      "CLASS_EDIT_ACTIVITY" -> (packageName + ".ui.Edit" + modelName + "Activity"),
      "CLASS_NAME_UNDERSCORED" -> Util.camelToUnderscore(Util.uncapitalize(modelName)),
      "MODEL_NAME" -> modelName
    )
  }

  private def applyTemplate(templateKeysForModel: Map[String, String], templateString: String) = {
    templateKeysForModel.foldLeft(templateString) {
      (resultingMenu, currentMapEntry) =>
        resultingMenu.replace(currentMapEntry._1, currentMapEntry._2)
    }
  }

  def menuContext(packageName: String, modelName: String) = packageName + ".ui." + modelName + "MainActivity"

  def scaffoldFromModel(classDirectory: File, sourceDirectory: File, scalaSourceDirectory: File, modelName: String) =
  {
    val packageName = Android.findPackageName(sourceDirectory)
    val modelsPath = new File(classDirectory.toString + "/" + packageName.replace('.', '/') + "/models/")

    val classLoader = new URLClassLoader(Array[URL](classDirectory.toURL))

    if (modelsPath.listFiles() == null)
    {
      throw new Exception("Models folder not found or is empty.")
    }

    if (modelsPath.listFiles().exists(_.getName() == modelName + ".class") == false)
    {
      throw new Exception("Model '" + modelName + "' not found.")
    }
    
    val modelClass = classLoader.loadClass(packageName + ".models." + modelName)

    val modelFields = modelClass.getDeclaredFields()

    // TODO: use this knowledge to scaffold activities and layouts.

    val templateKeysForModel = templateKeys(packageName, modelName, modelFields)


    // get list of files on the plugin's scaffold resources folder
    val filesAndContent = Util.getResourceFiles("scaffold/")


    filesAndContent.foreach {
      case (filePath, fileContent) => {
        val finalFilePath = new File(sourceDirectory.getPath() + "/" + applyTemplate(templateKeysForModel, filePath))
        val finalFileContent = applyTemplate(templateKeysForModel, fileContent)

        // TODO: define override\merge policies here.
        //if (finalFilePath.exists() == false)
        //{
          IO.write(finalFilePath, finalFileContent)
        //}
      }
    }



  }

  def findModels(classDirectory: File, sourceDirectory: File): Parser[Seq[String]] =
  {
    try
    {
      val packageName = Android.findPackageName(sourceDirectory)
      val modelsPath = new File(classDirectory.toString + "/" + packageName.replace('.', '/') + "/models/")

      val classLoader = new URLClassLoader(Array[URL](classDirectory.toURL))

      if (modelsPath.listFiles() != null)
      {
        val models = modelsPath.listFiles().map(modelPath => {
          modelPath.getName().split('.').head
        })

        val modelsWithoutInnerClasses = models.filter(_.contains('$') == false)
        sbt.complete.Parsers.spaceDelimited(modelsWithoutInnerClasses mkString " ")
      }
      else
      {
        sbt.complete.Parsers.spaceDelimited(" modelName")
      }
    }
    catch
    {
      // might not be an android project yet when this plugin is still just being loaded
      case _ : Throwable =>
        sbt.complete.Parsers.spaceDelimited(" modelName")
    }
  }
}