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

    IO.write(new File(scalaSourceDirectory.getPath() + "extracted.txt"), menuXMLPath(sourceDirectory, modelName).getPath)//modelClassFields.deep.mkString("\n"))
    //TODO: use this knowledge to scaffold activities and layouts.

    val modelNameUnderscored = Util.camelToUnderscore(Util.uncapitalize(modelName))

    //IO.write(new File(scalaSourceDirectory.getPath() + "extracted.txt"), menu(packageName, modelName).toString)

    Util.saveXML(menuXMLPath(sourceDirectory, modelName), menuXML(packageName, modelName))
  }


  private def menuXML(packageName: String, modelName: String) =
  {
    val toolsContext = packageName + ".ui." + modelName + "MainActivity"

    val modelNameUnderscored = Util.camelToUnderscore(Util.uncapitalize(modelName))

    val androidID = "@+id/menu_main_new_" + modelNameUnderscored

    val androidTitle = "@string/menu_main_new_" + modelNameUnderscored

    <menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:uiOptions="splitActionBarWhenNarrow"
    tools:context={toolsContext} >

    <item android:id={androidID}
          android:title={androidTitle}
          android:icon="@drawable/ic_menu_new"
          android:showAsAction="ifRoom" />
    </menu>
  }

  private def menuXMLPath(sourceDirectory: File, modelName: String) =
    new File(sourceDirectory.getPath() + "/main/res/menu/main_" + Util.camelToUnderscore(Util.uncapitalize(modelName)) + ".xml")


  def findModels(classDirectory: File, sourceDirectory: File): Parser[Seq[String]] =
  {
    try
    {
          val packageName = Android.findPackageName(sourceDirectory)
          val modelsPath = new File(classDirectory.toString + "/" + packageName.replace('.', '\\') + "/models/")

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