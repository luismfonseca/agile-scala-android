package agile.android

import sbt._
import java.io._
import java.net._
import scala.xml._
import sbt.complete._
import scala.collection.immutable.HashMap
import scala.collection.immutable.ListMap

object Model
{

  private def templateKeys(packageName: String, modelName: String, modelFieldsNameAndType: Seq[(String, String)], mappedIdType: String): ListMap[String, String] = 
    ListMap[String, String](
      "MODEL_IMPORTS" -> ("import scala.slick.lifted.MappedTo\n" + resolveImports(modelFieldsNameAndType.map(_._2.split("\\[")(0)))),
      "IMPORTS_TABLE" -> (resolveImports(modelFieldsNameAndType.map(_._2.split("\\[")(0)))),
      "INJECT_IMPLICITS_IF_NEEDED" -> implicitsForFields(modelFieldsNameAndType),
      "MODEL_FIELDS_COMMA_SEPERATED" -> (modelFieldsNameAndType.map({case (fieldName, fieldType) => fieldName + ": " + fieldType}).reduce(_ + ", " + _)),
      "TABLE_FIELDS_TUPLE" -> (modelFieldsNameAndType map(_._1) mkString ", "),
      "MODEL_NAME_AS_IS" -> modelName,
      "MAPPED_ID_TYPE" -> mappedIdType,
      "MODEL_NAME_PLURAL" -> (modelName + "s"),
      "DEFS_OF_FIELDS" -> defsOfFields(modelFieldsNameAndType dropRight 1),
      "DEF_OF_ID" -> defOfId(modelFieldsNameAndType last, mappedIdType),
      "PACKAGE_NAME_AS_DIR" -> packageName.replace('.', '/'),
      "PACKAGE_DB" -> (packageName + ".db"),
      "PACKAGE_MODELS" -> (packageName + ".models")
    )

  private def applyTemplate(templateKeys: ListMap[String, String], templateContent: String) =
  {
    templateKeys.foldLeft(templateContent) {
      (resultingString, currentMapEntry) =>
        resultingString.replace(currentMapEntry._1, currentMapEntry._2)
    }
  }

  private val knownImports = (HashMap
    (
      "Date" -> "import java.util.Date\n",
      "Calendar" -> "import java.util.Calendar\n",
      "URL" -> "import java.net.URL\n",
      "File" -> "import java.io.File\n",
      "HashMap" -> "import scala.collection.immutable.HashMap\n",
      "HashSet" -> "import scala.collection.immutable.HashSet\n",
      "IntMap" -> "import scala.collection.immutable.IntMap\n",
      "ListMap" -> "import scala.collection.immutable.ListMap\n",
      "ListSet" -> "import scala.collection.immutable.ListSet\n",
      "LongMap" -> "import scala.collection.immutable.LongMap\n",
      "NumericRange" -> "import scala.collection.immutable.NumericRange\n",
      "Stack" -> "import scala.collection.immutable.Stack\n",
      "TreeMap" -> "import scala.collection.immutable.TreeMap\n",
      "TreeSet" -> "import scala.collection.immutable.TreeSet\n"
    )).withDefaultValue("")

  // A very simple approach to resolve imports.
  // Complex cases such as Seq[Date] are not considered.
  private def resolveImports(types: Seq[String]): String = {

    types.foldLeft("") { _ + knownImports(_) }
  }

  private def defsOfFields(fieldsNameAndType: Seq[(String, String)]): String =
    fieldsNameAndType.map(
      {
        case (fname, ftype) =>
          "  def %s = column[%s](\"%s\")" format(fname, ftype, Util.camelToUnderscore(Util.uncapitalize(fname)).toUpperCase)
      }
    ) mkString "\n\n"

  private def defOfId(nameAndType: (String, String), mappedIdType: String): String =
  {
    val line = 
      if (mappedIdType == "Int")
      {
        "  def %s = column[%s](\"ID\", O.PrimaryKey, O.AutoInc)"
      }
      else
      {
        "  def %s = column[%s](\"ID\", O.PrimaryKey)"
      }

    val (name, typeWithConstructor) = nameAndType
    val typeWithoutConstructor = typeWithConstructor.split(" ")(0)

    line.format(name, typeWithoutConstructor)
  }

  def implicitsForFields(fieldsNameAndType: Seq[(String, String)]): String =
  {
    val implicits = for ((fieldName, fieldType) <- fieldsNameAndType)
    yield {
      (fieldName, fieldType) match {
        case (_, "Date") =>
          """  implicit def string2Date = MappedColumnType.base[Date, String](
            |    d => d.toString,
            |    string => new Date()
            |  )
            |""".stripMargin
        case _ => ""
      }
    }
    implicits mkString ""
  }

  def getFilePath(sourceDirectory: File, scalaSourceDirectory: File, modelName: String) =
    new File(
      Android.getModelsPath(sourceDirectory, scalaSourceDirectory).getPath + "/" + modelName + ".scala"
    )
  
  def isFieldAnId(field: String) = 
  {
    val fieldUnderscored = Util.camelToUnderscore(field)
    fieldUnderscored == "id" || fieldUnderscored.startsWith("id_") || fieldUnderscored.endsWith("_id") || fieldUnderscored.endsWith("_i_d")
  }

  // TODO: accept only known fields
  def generate(sourceDirectory: File, modelName: String, fields: Seq[String]): Seq[File] = {

    val fieldsWithTypes: Seq[(String, String)] = fields.map(_.split(":") match { case Array(fieldName, fieldType) => (fieldName.trim, fieldType.trim) })

    val packageName = Android.findPackageName(sourceDirectory)

    val (firstFieldName, firstFieldType) = fieldsWithTypes(0)

    val isFirstFieldId = isFieldAnId(firstFieldName)

    val finalFieldsWithTypes: Seq[(String, String)] = 
      if (isFirstFieldId) {
        (firstFieldName, modelName + "Id") +: fieldsWithTypes
      }
      else {
        fieldsWithTypes :+ (Util.uncapitalize(modelName) + "Id", modelName + "Id = new " + modelName + "Id(-1)")
      }

    val mappedToType =
      if (isFirstFieldId) {
        firstFieldType
      }
      else {
        "Int"
      }


    val templateKeysForModel = templateKeys(packageName, modelName, finalFieldsWithTypes, mappedToType)

    val filesAndContent = Util.getResourceFiles("model/")

    val resultingFiles = for ((filePath, fileContent) <- filesAndContent)
    yield {
      val finalFilePath = new File(sourceDirectory.getPath() + "/" + applyTemplate(templateKeysForModel, filePath))
      val finalFileContent = applyTemplate(templateKeysForModel, fileContent)

      IO.write(finalFilePath, finalFileContent, IO.utf8)
      finalFilePath
    }
    resultingFiles.toList

    /*var lines = Seq[String](
      packageName,
      "",
      imports,
      "class " + modelName + "Id(val value: " + mappedToType + ") extends MappedTo[" + mappedToType + "]",
      "",
      "case class " + modelName + "(" + finalFieldsWithTypes.map({case (fieldName, fieldType) => fieldName + ": " + fieldType}).reduce(_ + ", " + _) + ")",
      "{",
      "  ",
      "}")
    lines*/
  }
}
