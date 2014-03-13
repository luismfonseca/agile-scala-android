import sbt._
import sbt.complete._
import java.io._
import java.net._
import scala.xml._
import java.lang.ClassLoader
import java.lang.reflect.Field
import collection.immutable.ListMap

object Scaffold
{

  private def templateKeys(packageName: String, modelName: String, modelFields: Array[Field]) = {
    ListMap[String, String](
      "FRAGMENT_LAYOUT_FIELDS" -> applyTemplateOnFields("layout/fragment_show_", modelName, modelFields),
      "FRAGMENT_LAYOUT_EDIT_FIELDS" -> applyTemplateOnFields("layout/fragment_edit_", modelName, modelFields),
      "ITEM_LAYOUT_FIELDS" -> applyTemplateOnFields("layout/item_", modelName, modelFields),
      "TWO_OR_THREE_IF_ITEMS_CONTAINS_DATE" -> (if (modelFields.exists(_.getType() == classOf[java.util.Date])) "2" else "3"),
      "ITEM_MODEL_ATTRIBUTE_PLACEHOLDER_ID" -> "@+id/item_CLASS_NAME_UNDERSCORED_placeholder",
      "MENU_CONTEXT" -> menuContext(packageName, modelName),
      "MENU_ID" -> "@+id/menu_main_CLASS_NAME_UNDERSCORED",
      "MENU_TITLE" -> "@string/menu_main_new_CLASS_NAME_UNDERSCORED",
      "ID_EDIT_ACTIVITY" -> "@+id/edit_CLASS_NAME_UNDERSCORED_container",
      "CLASS_EDIT_ACTIVITY" -> (packageName + ".ui.Edit" + modelName + "Activity"),
      "CLASS_EDIT_FRAGMENT" -> (packageName + ".ui.Edit" + modelName + "Fragment"), 
      "CLASS_FRAGMENT" -> (packageName + ".ui." + modelName + "Fragment"),
      "FIELDS_COUNT_PLUS_ONE" -> (modelFields.size + 1).toString,
      "CLASS_NAME_UNDERSCORED" -> Util.camelToUnderscore(Util.uncapitalize(modelName)),
      "MODEL_NAME" -> modelName
    )
  }

  private def applyTemplate(templateKeysForModel: ListMap[String, String], templateString: String) = {
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


  private def templateFieldKeys(modelName: String, modelField: Field, index: Integer) =
    ListMap[String, String](
      "MODEL_ATTRIBUTE_NAME" -> "FIELD_NAME_PRETTY",
      "ITEM_MODEL_ATTRIBUTE_ID" -> "@+id/item_CLASS_NAME_UNDERSCORED_FIELD_NAME_UNDERSCORED",
      "MODEL_ATTRIBUTE_ID" -> "@+id/CLASS_NAME_UNDERSCORED_FIELD_NAME_UNDERSCORED",
      "MODEL_ATTRIBUTE_CREATE_ID" -> "@+id/create_CLASS_NAME_UNDERSCORED_FIELD_NAME_UNDERSCORED",
      "TEXT_BOLD_IF_FIRST_ELEMENT" -> (if (index == 0) "            android:textStyle=\"bold\"\n" else ""),
      "LAYOUT_ROW" -> ("" + index),
      "CLASS_NAME_UNDERSCORED" -> Util.camelToUnderscore(Util.uncapitalize(modelName)),
      "FIELD_NAME_UNDERSCORED" -> Util.camelToUnderscore(Util.uncapitalize(modelField.toString.split('.').last)),
      "FIELD_NAME_PRETTY" -> Util.camelToSpace(Util.uncapitalize(modelField.toString.split('.').last)),
      "MODEL_NAME" -> modelName
    )

  private def applyTemplateOnFields(templateType: String, modelName: String, modelFields: Array[Field]): String =
    modelFields.zipWithIndex.foldLeft("")
    {
      (lines, modelFieldAndIndex) =>
      {
        val (modelField, index) = modelFieldAndIndex
        val modelType: String = modelField.getType().toString().split('.').last

        val template = getClass.getClassLoader().getResourceAsStream("scaffold-partial-elements/" + templateType + modelType)
        if (template == null)
        {
          throw new Exception("Unsupported field type: " + modelType)
        }

        lines + applyTemplate(templateFieldKeys(modelName, modelField, index), Util.convertStreamToString(template))
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