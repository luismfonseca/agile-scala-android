package agile.android

import sbt._
import sbt.complete._
import java.io._
import java.net._
import scala.xml._
import java.lang.ClassLoader
import java.lang.reflect.Field
import collection.immutable.ListMap
import scala.util.Random

object Scaffold
{

  private def templateKeys(packageName: String, modelName: String, modelFields: Array[Field]) = {
    ListMap[String, String](
      "FRAGMENT_LAYOUT_FIELDS" -> applyTemplateOnFields("layout/fragment_show_", modelName, modelFields),
      "FRAGMENT_LAYOUT_EDIT_FIELDS" -> applyTemplateOnFields("layout/fragment_edit_", modelName, modelFields),
      "ITEM_LAYOUT_FIELDS" -> applyTemplateOnFields("layout/item_", modelName, modelFields),
      "IMPORT_MODEL_FIELDS_DEPENDENCIES" -> applyTemplateOnFields("scala/import_", modelName, modelFields), // do import only once
      "IMPORT_EDIT_FRAGMENT_FIELDS_DEPENDENCIES" ->  applyTemplateOnFields("scala/EditFragment/import_", modelName, modelFields), // do import only once
      "FRAGMENT_VIEW_FIELDS" -> applyTemplateOnFields("scala/fragment_view_", modelName, modelFields),
      "FRAGMENT_EDIT_FIELDS" -> applyTemplateOnFields("scala/EditFragment/field_", modelName, modelFields),
      "FRAGMENT_VIEW_ASSIGN_FIELDS" -> applyTemplateOnFields("scala/fragment_view_assign_", modelName, modelFields),
      "FRAGMENT_EDIT_VIEW_ASSIGN_FIELDS" ->  applyTemplateOnFields("scala/EditFragment/field_assign_", modelName, modelFields),
      "FRAGMENT_VIEW_DISPLAY_FIELDS" -> applyTemplateOnFields("scala/fragment_view_display_", modelName, modelFields),
      "FRAGMENT_EDIT_VIEW_GET_FIELDS" -> applyTemplateOnFields("scala/EditFragment/view_get_", modelName, modelFields),
      "FRAGMENT_EDIT_VIEW_SET_FIELDS" -> applyTemplateOnFields("scala/EditFragment/view_set_", modelName, modelFields),
      "RANDOM_DATA_COMMA_SEPARATED" ->  applyTemplateOnFields("scala/RandomData/comma_", modelName, modelFields),
      "LIST_ADAPTER_VIEWHOLDER_ELEMENTS" -> applyTemplateOnFields("scala/ListAdapter/viewholder_element_", modelName, modelFields),
      "LIST_ADAPTER_VIEWHOLDER_PARAMETERS" -> applyTemplateOnFields("scala/ListAdapter/viewholder_parameters_", modelName, modelFields),
      "VIEWHOLDER_DISPLAY_FIELDS" ->  applyTemplateOnFields("scala/ListAdapter/viewholder_display_", modelName, modelFields),
      "TWO_OR_THREE_IF_ITEMS_CONTAINS_DATE" -> (if (modelFields.exists(_.getType() == classOf[java.util.Date])) "3" else "2"),
      "ITEM_MODEL_ATTRIBUTE_PLACEHOLDER_ID" -> "@+id/item_CLASS_NAME_UNDERSCORED_placeholder",
      "MENU_CONTEXT" -> menuContext(packageName, modelName),
      "MENU_ID" -> "@+id/menu_main_CLASS_NAME_UNDERSCORED",
      "MENU_TITLE" -> "@string/menu_main_new_CLASS_NAME_UNDERSCORED",
      "ID_EDIT_ACTIVITY" -> "@+id/edit_CLASS_NAME_UNDERSCORED_container",
      "ID_MAIN_ACTIVITY" -> "@+id/CLASS_NAME_UNDERSCORED_main_container",
      "CLASS_MAIN_ACTIVITY" -> (packageName + ".ui." + modelName + "Activity"),
      "CLASS_EDIT_ACTIVITY" -> (packageName + ".ui.Edit" + modelName + "Activity"),
      "CLASS_EDIT_FRAGMENT" -> (packageName + ".ui.Edit" + modelName + "Fragment"), 
      "CLASS_FRAGMENT" -> (packageName + ".ui." + modelName + "Fragment"),
      "FIELDS_COUNT_PLUS_ONE" -> (modelFields.size + 1).toString,
      "CLASS_NAME_UNDERSCORED" -> Util.camelToUnderscore(Util.uncapitalize(modelName)),
      "CLASS_NAME_AS_IS" -> modelName,
      "CLASS_NAME_UNCAPITALIZED" -> Util.uncapitalize(modelName),
      "MODEL_NAME_PRETTY" -> Util.camelToSpace(Util.uncapitalize(modelName)),
      "PACKAGE_NAME_AS_DIR" -> packageName.replace('.', '/'),
      "PACKAGE_UI" -> (packageName + ".ui"),
      "PACKAGE_R" -> (packageName + ".R"),
      "PACKAGE_MODELS" -> (packageName + ".models")
    )
  }

  private def applyTemplate(templateKeysForModel: ListMap[String, String], templateString: String) = {
    templateKeysForModel.foldLeft(templateString) {
      (resultingMenu, currentMapEntry) =>
        resultingMenu.replace(currentMapEntry._1, currentMapEntry._2)
    }
  }

  def menuContext(packageName: String, modelName: String) = packageName + ".ui." + modelName + "MainActivity"

  def scaffoldFromModel(classDirectory: File, sourceDirectory: File, scalaSourceDirectory: File, externalDependencyClasspath: Seq[Attributed[File]], modelName: String) =
  {
    val packageName = Android.findPackageName(sourceDirectory)
    
    val modelsPath = new File(classDirectory.toString + "/" + packageName.replace('.', '/') + "/models/")

    val externalJars: Array[URL] = externalDependencyClasspath.map(_.data.toURL).toArray

    val classLoader = new URLClassLoader(Array[URL](classDirectory.toURL) ++ externalJars)

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

    val modelFieldsWithoutIds = modelFields.filter(field => Model.isFieldAnId(field.toString.split('.').last) == false)

    // TODO: use this knowledge to scaffold activities and layouts.
    val templateKeysForModel = templateKeys(packageName, modelName, modelFieldsWithoutIds)

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

    // files that need merging
    val filesAndContentToMerge = Util.getResourceFiles("scaffold-merge/")
    filesAndContentToMerge.foreach {
      case (filePath, fileContent) => {
        val finalFilePath = new File(sourceDirectory.getPath() + "/" + applyTemplate(templateKeysForModel, filePath))
        val partialFileContent = applyTemplate(templateKeysForModel, fileContent)

        // load existing file, if any
        val finalFileContent =
          if (finalFilePath.exists)
          {
            // NOTE: assuming xml files only
            val originalFileContent = XML.loadFile(finalFilePath)

            // TODO: enforce override\merge policies here.
            if (finalFilePath.getPath.endsWith("AndroidManifest.xml"))
            {
              val originalApplicationNode = originalFileContent.child.filter(_.label == "application")(0)
              val partialApplicationNode = XML.loadString(partialFileContent).child.filter(_.label == "application")(0).child(1)
              val newApplicationElement = Util.appendNodesXML(
                originalApplicationNode,
                partialApplicationNode
              )

              originalFileContent.copy(child = originalFileContent.child.filterNot(_.label == "application") :+ newApplicationElement)
            }
            else
            {
              Util.mergeXML(originalFileContent, XML.loadString(partialFileContent), "name", false)
            }
          }
          else
          {
            XML.loadString(partialFileContent mkString "")
          }

        IO.write(finalFilePath, finalFileContent.toString)
      }
    }

    // raw files
    val filesAndContentRaw = Util.getResourceFilesRaw("scaffold-raw/")
    filesAndContentRaw.foreach {
      case (filePath, finalFileContent) => {
        val finalFilePath = new File(sourceDirectory.getPath() + "/" + applyTemplate(templateKeysForModel, filePath))

        // TODO: enforce override\merge policies here.
        if (finalFilePath.exists == false)
        {
          IO.write(finalFilePath, finalFileContent)
        }
      }
    }

  }


  private def templateFieldKeys(fieldName: String, modelField: Field, index: Integer) =
    ListMap[String, String](
      "MODEL_ATTRIBUTE_NAME" -> "FIELD_NAME_PRETTY",
      "ITEM_MODEL_ATTRIBUTE_ID" -> "@+id/item_CLASS_NAME_UNDERSCORED_FIELD_NAME_UNDERSCORED",
      "MODEL_ATTRIBUTE_ID" -> "@+id/CLASS_NAME_UNDERSCORED_FIELD_NAME_UNDERSCORED",
      "MODEL_ATTRIBUTE_CREATE_ID" -> "@+id/create_CLASS_NAME_UNDERSCORED_FIELD_NAME_UNDERSCORED",
      "TEXT_BOLD_IF_FIRST_ELEMENT" -> (if (index == 0) "            android:textStyle=\"bold\"\n" else ""),
      "COMMA_IF_NOT_FIRST " -> (if (index == 0) "" else ", "),
      "COMMA_NEW_LINE_IF_NOT_FIRST" -> (if (index == 0) "" else ",\n"),
      "LAYOUT_ROW" -> ("" + index),
      "LINT_PROPER_INPUT_TYPE" -> getProperInputType(Util.camelToUnderscore(Util.uncapitalize(modelField.toString.split('.').last))),
      "CLASS_NAME_UNDERSCORED" -> Util.camelToUnderscore(Util.uncapitalize(fieldName)),
      "FIELD_NAME_UNDERSCORED" -> Util.camelToUnderscore(Util.uncapitalize(modelField.toString.split('.').last)),
      "FIELD_NAME_PRETTY" -> Util.camelToSpace(Util.uncapitalize(modelField.toString.split('.').last)),
      "MODEL_NAME" -> fieldName,
      "FIELD_NAME_AS_IS" -> modelField.toString.split('.').last,
      "FIELD_NAME_CAPITALIZED" -> Util.capitalize(modelField.toString.split('.').last),
      "RANDOM_INT" -> new Random(index).nextInt(10).toString()
    )

  private def applyTemplateOnFields(templateType: String, fieldName: String, modelFields: Array[Field]): String =
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

        lines + applyTemplate(templateFieldKeys(fieldName, modelField, index), Util.convertStreamToString(template))
      }
    }

  private def getProperInputType(fieldName: String): String = 
    if (fieldName.contains("password"))
    {
      "textPassword"
    }
    else if (fieldName.contains("url"))
    {
      "textUri"
    }
    else if (fieldName.contains("email"))
    {
      "textEmailAddress"
    }
    else if (fieldName.contains("_pin") || fieldName.contains("pin_"))
    {
      "numberPassword"
    }
    else {
      "text"
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