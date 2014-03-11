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

    //TODO: use this knowledge to scaffold activities and layouts.

    val templateKeysForModel = templateKeys(packageName, modelName, modelFields)

    IO.write(menuXMLPath(sourceDirectory, modelName), applyTemplate(templateKeysForModel, menuXMLContent))

    IO.write(layoutXMLPath(sourceDirectory), layoutXMLContent)

    IO.write(layoutActivityEditXMLPath(sourceDirectory, templateKeysForModel), applyTemplate(templateKeysForModel, layoutActivityEditXMLContent))

    if (layoutActionBarEditCancelDoneXMLPath(sourceDirectory).exists() == false)
    {
      IO.write(layoutActionBarEditCancelDoneXMLPath(sourceDirectory), layoutActionBarEditCancelDoneXMLContent)
    }
    if (animatorSlideInPath(sourceDirectory).exists() == false)
    {
      IO.write(animatorSlideInPath(sourceDirectory), animatorSlideInContent)
    }
    if (animatorSlideOutPath(sourceDirectory).exists() == false)
    {
      IO.write(animatorSlideOutPath(sourceDirectory), animatorSlideOutContent)
    }


    //IO.write(new File(scalaSourceDirectory.getPath() + "extracted.txt"), scaffoldedMenu)
  }

  private def menuXMLPath(sourceDirectory: File, modelName: String) =
    new File(sourceDirectory.getPath() + "/main/res/menu/main_" + Util.camelToUnderscore(Util.uncapitalize(modelName)) + ".xml")

  private def layoutXMLPath(sourceDirectory: File) =
    new File(sourceDirectory.getPath() + "/main/res/layout/main_activity.xml")

  private def layoutActivityEditXMLPath(sourceDirectory: File, template: Map[String, String]) =
    new File(sourceDirectory.getPath() + applyTemplate(template, "/main/res/layout/activity_edit_CLASS_NAME_UNDERSCORED.xml"))

  private def layoutActionBarEditCancelDoneXMLPath(sourceDirectory: File) =
    new File(sourceDirectory.getPath() + "/main/res/layout/actionbar_edit_cancel_done.xml")

  private def animatorSlideInPath(sourceDirectory: File) =
    new File(sourceDirectory.getPath() + "/main/res/animator/slide_in.xml")

  private def animatorSlideOutPath(sourceDirectory: File) =
    new File(sourceDirectory.getPath() + "/main/res/animator/slide_out.xml")

  private def menuXMLContent =
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("scaffold/menu/main.xml"))

  private def layoutXMLContent =
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("scaffold/layout/activity_main.xml"))

  private def layoutActivityEditXMLContent =
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("scaffold/layout/activity_edit_CLASS_NAME_UNDERSCORED.xml"))

  private def layoutActionBarEditCancelDoneXMLContent =
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("scaffold/layout/actionbar_edit_cancel_done.xml"))

  private def animatorSlideInContent =
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("scaffold/animator/slide_in.xml"))

  private def animatorSlideOutContent =
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("scaffold/animator/slide_out.xml"))

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

        sbt.complete.Parsers.spaceDelimited(models mkString " ")
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