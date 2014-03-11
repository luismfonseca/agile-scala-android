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
      "CLASS_NAME_UNDERSCORED" -> Util.camelToUnderscore(Util.uncapitalize(modelName))
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

    val scaffoldedMenu = applyTemplate(templateKeysForModel, menuXML(packageName, modelName))


    IO.write(new File(scalaSourceDirectory.getPath() + "extracted.txt"), scaffoldedMenu)
  }

  private def menuXMLPath(sourceDirectory: File, modelName: String) =
    new File(sourceDirectory.getPath() + "/main/res/menu/main_" + Util.camelToUnderscore(Util.uncapitalize(modelName)) + ".xml")

  private def menuXML(packageName: String, modelName: String) =
  {
    val toolsContext = packageName + ".ui." + modelName + "MainActivity"

    val modelNameUnderscored = Util.camelToUnderscore(Util.uncapitalize(modelName))

    val androidID = "@+id/menu_main_new_" + modelNameUnderscored

    val androidTitle = "@string/menu_main_new_" + modelNameUnderscored

    /*<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:uiOptions="splitActionBarWhenNarrow"
    tools:context={toolsContext} >

    <item android:id={androidID}
          android:title={androidTitle}
          android:icon="@drawable/ic_menu_new"
          android:showAsAction="ifRoom" />
    </menu>*/
    Util.convertStreamToString(getClass.getClassLoader().getResourceAsStream("scaffold/menu/main.xml"))
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