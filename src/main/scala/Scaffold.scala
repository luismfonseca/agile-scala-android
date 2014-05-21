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

import ModelGenerator.Model
import ModelGenerator.ModelField

object Scaffold
{

  private def templateKeys(packageName: String, model: Model, modelWithoutOtherModels: Model, modelsOfModel: Array[Model]) = {
    ListMap[String, String](
      "FRAGMENT_EDIT_ON_ACTIVITY_RESULT_IF_NEEDED" -> (if (modelsOfModel.isEmpty) "" else Util.getResourceFile("scaffold-partial-elements/scala/EditFragment/onActivityResult")),
      "FRAGMENT_EDIT_ON_ACTIVITY_RESULT_HANDLERS" -> applyTemplateOnFieldsThatAreModels("scala/EditFragment/onActivityResult_", model, modelsOfModel),
      "FRAGMENT_LAYOUT_FIELDS_OTHER_MODELS" -> applyTemplateOnFieldsThatAreModels("layout/fragment_show_", model, modelsOfModel),
      "FRAGMENT_LAYOUT_FIELDS" -> applyTemplateOnFields("layout/fragment_show_", modelWithoutOtherModels),
      "FRAGMENT_LAYOUT_EDIT_FIELDS_OTHER_MODELS" -> applyTemplateOnFieldsThatAreModels("layout/fragment_edit_", model, modelsOfModel),
      "FRAGMENT_LAYOUT_EDIT_FIELDS" ->  applyTemplateOnFields("layout/fragment_edit_", modelWithoutOtherModels),
      "IMPORT_OTHER_MODELS_UI_NO_ARRAYS" -> resolveImportsOfOtherModelsUI(model, modelsOfModel, false),
      "IMPORT_OTHER_MODELS_UI" -> resolveImportsOfOtherModelsUI(model, modelsOfModel, true),
      "IMPORT_OTHER_MODELS" -> resolveImportsOfOtherModels(modelsOfModel),
      "ITEM_LAYOUT_FIELDS" -> applyTemplateOnFields("layout/item_", modelWithoutOtherModels),
      "IMPORT_MODEL_FIELDS_DEPENDENCIES" -> applyTemplateOnFieldsUniqueType("scala/import_", modelWithoutOtherModels),
      "IMPORT_EDIT_FRAGMENT_FIELDS_DEPENDENCIES" ->  applyTemplateOnFieldsUniqueType("scala/EditFragment/import_", modelWithoutOtherModels),
      "FRAGMENT_VIEW_OTHER_MODELS_FIELDS" -> applyTemplateOnFieldsThatAreModels("scala/fragment_view_", model, modelsOfModel),
      "FRAGMENT_EDIT_OTHER_MODELS_FIELDS" -> applyTemplateOnFieldsThatAreModels("scala/EditFragment/field_", model, modelsOfModel),
      "FRAGMENT_EDIT_ACTIVITIES_REQUEST_MODELS_ID" -> applyTemplateOnFieldsThatAreModels("scala/fragment_edit_activity_request_id_", model, modelsOfModel),
      "FRAGMENT_VIEW_FIELDS" -> applyTemplateOnFields("scala/fragment_view_", modelWithoutOtherModels),
      "FRAGMENT_EDIT_FIELDS" -> applyTemplateOnFields("scala/EditFragment/field_", modelWithoutOtherModels),
      "FRAGMENT_VIEW_ASSIGN_FIELDS_OTHER_MODELS" -> applyTemplateOnFieldsThatAreModels("scala/fragment_view_assign_", model, modelsOfModel),
      "FRAGMENT_EDIT_ASSIGN_FIELDS_OTHER_MODELS" -> applyTemplateOnFieldsThatAreModels("scala/EditFragment/field_assign_", model, modelsOfModel),
      "FRAGMENT_VIEW_ASSIGN_FIELDS" -> applyTemplateOnFields("scala/fragment_view_assign_", modelWithoutOtherModels),
      "FRAGMENT_EDIT_ASSIGN_FIELDS" ->  applyTemplateOnFields("scala/EditFragment/field_assign_", modelWithoutOtherModels),
      "FRAGMENT_VIEW_DISPLAY_FIELDS" -> applyTemplateOnFields("scala/fragment_view_display_", modelWithoutOtherModels),
      "FRAGMENT_VIEW_DISPLAY_OTHER_MODELS_FIELDS" -> applyTemplateOnFieldsThatAreModels("scala/fragment_view_display_", model, modelsOfModel),
      "FRAGMENT_EDIT_DISPLAY_OTHER_MODELS_FIELDS" -> applyTemplateOnFieldsThatAreModels("scala/EditFragment/display_", model, modelsOfModel),
      "FRAGMENT_EDIT_VIEW_GET_FIELDS" -> applyTemplateOnFields("scala/EditFragment/view_get_", model, None, Some(modelsOfModel)),
      "FRAGMENT_EDIT_VIEW_SET_FIELDS" -> applyTemplateOnFields("scala/EditFragment/view_set_", modelWithoutOtherModels),
      "RANDOM_DATA_COMMA_SEPARATED" -> modelConstructorWithRandomData(model, modelsOfModel),
      "LIST_ADAPTER_VIEWHOLDER_ELEMENTS" -> applyTemplateOnFields("scala/ListAdapter/viewholder_element_", modelWithoutOtherModels),
      "LIST_ADAPTER_VIEWHOLDER_PARAMETERS" -> applyTemplateOnFields("scala/ListAdapter/viewholder_parameters_", modelWithoutOtherModels),
      "VIEWHOLDER_DISPLAY_FIELDS" ->  applyTemplateOnFields("scala/ListAdapter/viewholder_display_", modelWithoutOtherModels),
      "TWO_OR_THREE_IF_ITEMS_CONTAINS_DATE" -> (if (modelWithoutOtherModels.fields.exists(_.typeSimple == "Date")) "3" else "2"),
      "ITEM_MODEL_ATTRIBUTE_PLACEHOLDER_ID" -> "@+id/item_CLASS_NAME_UNDERSCORED_placeholder",
      "MENU_CONTEXT" -> menuContext(packageName, model.name),
      "MENU_ID" -> "@+id/menu_main_CLASS_NAME_UNDERSCORED",
      "MENU_TITLE" -> "@string/menu_main_new_CLASS_NAME_UNDERSCORED",
      "ID_EDIT_ACTIVITY" -> "@+id/edit_CLASS_NAME_UNDERSCORED_container",
      "ID_MAIN_ACTIVITY" -> "@+id/CLASS_NAME_UNDERSCORED_main_container",
      "CLASS_MAIN_ACTIVITY" -> (packageName + ".ui.CLASS_NAME_UNDERSCORED." + model.name + "Activity"),
      "CLASS_EDIT_ACTIVITY" -> (packageName + ".ui.CLASS_NAME_UNDERSCORED.Edit" + model.name + "Activity"),
      "CLASS_EDIT_FRAGMENT" -> (packageName + ".ui.CLASS_NAME_UNDERSCORED.Edit" + model.name + "Fragment"), 
      "CLASS_FRAGMENT" -> (packageName + ".ui.CLASS_NAME_UNDERSCORED." + model.name + "Fragment"),
      "FIELDS_COUNT_PLUS_ONE" -> (modelWithoutOtherModels.fields.size + 1).toString,
      "CLASS_NAME_UNDERSCORED" -> Util.camelToUnderscore(Util.uncapitalize(model.name)),
      "CLASS_NAME_AS_IS" -> model.name,
      "CLASS_NAME_UNCAPITALIZED" -> Util.uncapitalize(model.name),
      "MODEL_NAME_PRETTY" -> Util.camelToSpace(Util.uncapitalize(model.name)),
      "PACKAGE_NAME_AS_DIR" -> packageName.replace('.', '/'),
      "PACKAGE_UI" -> (packageName + ".ui"),
      "PACKAGE_R" -> (packageName + ".R"),
      "PACKAGE_TR" -> (packageName + ".TR"),
      "PACKAGE_TYPEDRESOURCE_IMPLICITS" -> (packageName + ".TypedResource._"),
      "PACKAGE_MODELS" -> (packageName + ".models")
    )
  }

  private def applyTemplate(templateKeysForModel: ListMap[String, String], templateString: String): String = {
    templateKeysForModel.foldLeft(templateString) {
      (resultingMenu, currentMapEntry) =>
        resultingMenu.replace(currentMapEntry._1, currentMapEntry._2)
    }
  }

  private def modelConstructorWithRandomData(model: Model, otherModels: Array[Model]): String = {
    model.fields.zipWithIndex.foldLeft("")
    {
      (lines, modelFieldAndIndex) =>
      {
        val (modelField, index) = modelFieldAndIndex

        val template = getClass.getClassLoader().getResourceAsStream("scaffold-partial-elements/scala/RandomData/comma_" + modelField.typeSimple)
        if (template == null)
        {
          // Check if it's a model constructor.
          //if (modelFieldsOfModels exists(_.getType().toString().split('.').last == modelType))
          //  applyTemplateOnFields("scala/RandomData/comma_", modelName, )
          //throw new Exception("Unsupported field type: " + modelField.typeSimple)
          lines + (if (index == 0) "null" else ", null")
        }
        else
        {
          lines + applyTemplate(templateFieldKeys(index, model.name, modelField), Util.convertStreamToString(template))
        }
      }
    }
  }

  private def resolveImportsOfOtherModels(otherModels: Array[Model]): String =
  {
    val otherModelsNames = otherModels.aggregate(Array[String]())(_ :+ _.name, _ ++ _).distinct

    otherModelsNames map("import PACKAGE_MODELS." + _) mkString "\n"
  }

  private def resolveImportsOfOtherModelsUI(model: Model, otherModels: Array[Model], handleArray: Boolean): String =
  {
    val otherModelsUINames = model.fields
      .filter(modelField => modelField.isArray == handleArray && otherModels.exists(_.name == modelField.typeSimple))
      .aggregate(Array[String]())(_ :+ _.typeSimple, _ ++ _).distinct

    if (otherModelsUINames.size == 0)
    {
      ""
    }
    else
    {
      (if (handleArray) "import PACKAGE_UI.ChangeToFragmentHandler\n" else "") +
        (otherModelsUINames map(modelName => "import PACKAGE_UI." + Util.camelToUnderscore(Util.uncapitalize(modelName)) + "._") mkString "\n")
    }
  }

  def menuContext(packageName: String, modelName: String) = packageName + ".ui." + modelName + "MainActivity"

  def scaffoldFromModel(classDirectory: File, sourceDirectory: File, scalaSourceDirectory: File, externalDependencyClasspath: Seq[Attributed[File]], modelName: String): Unit =
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

    val existingModels = Android.getModels(sourceDirectory)

    val model = ModelGenerator.loadModel(classLoader, packageName, modelName)

    val allModels = existingModels map(ModelGenerator.loadModel(classLoader, packageName, _))

    val modelsToScaffold = modelDependecies(model, allModels).filterNot(_ == model)

    scaffoldModel(sourceDirectory, classLoader, packageName, model, existingModels, true)
    
    modelsToScaffold.foreach(model => scaffoldModel(sourceDirectory, classLoader, packageName, model, existingModels, false))
  }

  private def modelDependecies(model: Model, allModels: Seq[Model], modelDependeciesFound: Array[Model] = Array[Model]()): Seq[Model] =
  {
    val modelsInModel = model.fields
      .filter(modelField => (allModels.exists(model => (modelField.typeSimple == model.name))))
      .map(modelField => allModels.find(model => modelField.typeSimple == model.name).get)
      .distinct

    val modelNotYetFound = modelsInModel.filterNot(modelDependeciesFound contains)

    model +: modelNotYetFound.flatMap(modelDependecies(_, allModels, modelDependeciesFound :+ model))
  }

  private def scaffoldModel(sourceDirectory: File, classLoader: ClassLoader, packageName: String, model: Model, existingModels: Seq[String], overrideExistingFiles: Boolean): Unit = {

    val modelNamesInModel = model.fields map(_.typeSimple) filter(existingModels contains)

    val modelWithoutIds = model.copy(fields = model.fields.filterNot(field => ModelGenerator.isFieldAnId(field.typeSimple)))

    val modelWithoutOtherModels = modelWithoutIds.copy(fields = modelWithoutIds.fields filterNot(modelNamesInModel contains _.typeSimple))

    val modelsInModel: Array[Model] = modelNamesInModel map(ModelGenerator.loadModel(classLoader, packageName, _))

    val templateKeysForModel = templateKeys(packageName, modelWithoutIds, modelWithoutOtherModels, modelsInModel)

    // get list of files on the plugin's scaffold resources folder
    val filesAndContent = Util.getResourceFiles("scaffold/")

    filesAndContent.foreach {
      case (filePath, fileContent) => {
        val finalFilePath = new File(sourceDirectory.getPath() + "/" + applyTemplate(templateKeysForModel, filePath))
        val finalFileContent = applyTemplate(templateKeysForModel, fileContent)

        if (finalFilePath.exists() == false || overrideExistingFiles)
        {
          IO.write(finalFilePath, finalFileContent)
        }
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

        // always merge.
        //if (finalFilePath.exists() == false || overrideExistingFiles)
        //{
          IO.write(finalFilePath, finalFileContent.toString)
        //}
      }
    }

    // raw files
    val filesAndContentRaw = Util.getResourceFilesRaw("scaffold-raw/")
    filesAndContentRaw.foreach {
      case (filePath, finalFileContent) => {
        val finalFilePath = new File(sourceDirectory.getPath() + "/" + applyTemplate(templateKeysForModel, filePath))

        if (finalFilePath.exists() == false || overrideExistingFiles)
        {
          IO.write(finalFilePath, finalFileContent)
        }
      }
    }
  }

  private def templateFieldKeys(index: Integer, modelName: String, field: ModelField, foreignModel: Option[Model] = None, parentFieldName: Option[String] = None) =
    ListMap[String, String](
      "FRAGMENT_VIEW_ASSIGN_FIELDS_OF_THIS_MODEL" ->
      (
        foreignModel match {
          case Some(model) => applyTemplateOnFields("scala/fragment_view_assign_", model, Some(field.name))
          case _ => ""
        }
      ),
      "FRAGMENT_VIEW_FIELDS_OF_THIS_MODEL" ->
      (
        foreignModel match {
          case Some(model) => applyTemplateOnFields("scala/fragment_view_", model, Some(field.name))
          case _ => ""
        }
      ),
      "FRAGMENT_VIEW_DISPLAY_FIELDS_OF_THIS_MODEL" ->
      (
        foreignModel match {
          case Some(model) => applyTemplateOnFields("scala/fragment_view_display_", model, Some(field.name))
          case _ => ""
        }
      ),
      "FRAGMENT_EDIT_FIELDS_OF_THIS_MODEL" ->
      (
        foreignModel match {
          case Some(model) => applyTemplateOnFields("scala/fragment_view_", model, Some(field.name))
          case _ => ""
        }
      ),
      "FRAGMENT_EDIT_ASSIGN_FIELDS_OF_THIS_MODEL" ->
      (
        foreignModel match {
          case Some(model) => applyTemplateOnFields("scala/fragment_view_assign_", model, Some(field.name))
          case _ => ""
        }
      ),
      "MODEL_ATTRIBUTE_NAME" -> "FIELD_NAME_PRETTY",
      "ITEM_MODEL_ATTRIBUTE_ID" -> "@+id/item_CLASS_NAME_UNDERSCORED_FIELD_NAME_UNDERSCORED",
      "MODEL_ATTRIBUTE_ID" -> "@+id/CLASS_NAME_UNDERSCORED_FIELD_NAME_UNDERSCORED",
      "MODEL_ATTRIBUTE_CREATE_ID" -> "@+id/create_CLASS_NAME_UNDERSCORED_FIELD_NAME_UNDERSCORED",
      "TEXT_BOLD_IF_FIRST_ELEMENT" -> (if (index == 0) "            android:textStyle=\"bold\"\n" else ""),
      "COMMA_IF_NOT_FIRST " -> (if (index == 0) "" else ", "),
      "COMMA_NEW_LINE_IF_NOT_FIRST" -> (if (index == 0) "" else ",\n"),
      "LAYOUT_ROW" -> index.toString,
      "FIELD_INDEX" -> index.toString,
      "MODEL_PREFIX" -> 
      (
        parentFieldName match {
          case Some(name) => Util.capitalize(name)
          case _ => Util.capitalize(modelName)
        }
      ),
      "LINT_PROPER_INPUT_TYPE" -> getProperInputType(Util.camelToUnderscore(Util.uncapitalize(field.name))),
      "CLASS_NAME_UNDERSCORED" -> Util.camelToUnderscore(Util.uncapitalize(modelName)),
      "FIELD_NAME_UNDERSCORED" -> Util.camelToUnderscore(Util.uncapitalize(field.name)),
      "FIELD_TYPE_UNDERSCORED" -> Util.camelToUnderscore(Util.uncapitalize(field.typeSimple)),
      "FIELD_NAME_PRETTY" -> Util.camelToSpace(Util.uncapitalize(field.name)),
      "FIELD_TYPE_PRETTY" -> Util.camelToSpace(Util.uncapitalize(field.typeSimple)),
      "MODEL_NAME" -> modelName,
      "FIELD_NAME_UPPERCASE" -> field.name.toUpperCase,
      "PARENT_FIELD_NAME_AS_IS" -> parentFieldName.getOrElse(Util.uncapitalize(modelName)),
      "FIELD_NAME_AS_IS" -> field.name,
      "FIELD_TYPE_AS_IS" -> field.typeSimple,
      "FIELD_NAME_FULLY_QUALIFIED" -> 
      (
        parentFieldName match {
          case Some(name) => (name + "." + field.name)
          case _ => field.name
        }
      ),
      "CLASS_NAME_UNCAPITALIZED" -> Util.uncapitalize(modelName),
      "FIELD_NAME_CAPITALIZED" -> Util.capitalize(field.name),
      "RANDOM_INT" -> new Random(index).nextInt(10).toString()
    )

  private def applyTemplateOnFields(templateType: String, model: Model, parentFieldName: Option[String] = None, otherModels: Option[Array[Model]] = None): String =
    model.fields.zipWithIndex.foldLeft("")
    {
      (lines, modelFieldAndIndex) =>
      {
        val (modelField, index) = modelFieldAndIndex
        val fieldType =
          if (otherModels.isDefined) {
            if (otherModels.get.exists(_.name == modelField.typeSimple)) {
              "Model"
            }
            else {
              modelField.typeSimple
            }
          }
          else {
            modelField.typeSimple
          }

        val template = getClass.getClassLoader().getResourceAsStream("scaffold-partial-elements/" + templateType + fieldType)
        if (template == null)
        {
          if (parentFieldName.isDefined)
          {
            lines
          }
          else
          {
            throw new Exception("Unsupported field type: " + fieldType + "\n" + templateType + fieldType + "\n")
          }
        }
        else
        {
          lines + applyTemplate(templateFieldKeys(index, model.name, modelField, None, parentFieldName), Util.convertStreamToString(template))
        }
      }
    }

  private def applyTemplateOnFieldsUniqueType(templateType: String, model: Model): String =
  {
    val distinctModelFieldsByType = model.fields.map(_.typeSimple).distinct.map(ModelField(".", _, false))
    applyTemplateOnFields(templateType, Model(".", distinctModelFieldsByType))
  }

  private def applyTemplateOnFieldsThatAreModels(templateType: String, model: Model, modelsOfModel: Array[Model]): String =
  {
    val nonModelFieldsSize: Int = model.fields.size - modelsOfModel.size
    
    val fields = model.fields filter(field => modelsOfModel exists(field.typeSimple == _.name))

    fields.zipWithIndex.foldLeft("")
    {
      (lines, modelFieldAndIndex) =>
      {
        val (modelField, index) = modelFieldAndIndex

        val suffixIfArray = if (modelField.isArray) "_Array" else ""

        val template = getClass.getClassLoader().getResourceAsStream("scaffold-partial-elements/" + templateType + "Model" + suffixIfArray)
        if (template == null)
        {
          throw new Exception("Unsupported field type: " + modelField.typeSimple + "\n" + templateType)
        }
        else
        {
          val foreignModel = modelsOfModel.find(modelField.typeSimple == _.name)
          lines + applyTemplate(templateFieldKeys(index + nonModelFieldsSize, model.name, modelField, foreignModel), Util.convertStreamToString(template))
        }
      }
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
