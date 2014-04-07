package agile.android

import sbt._
import java.io._
import java.net._
import scala.xml._
import sbt.complete._
import collection.immutable.ListMap
import scala.collection.immutable.HashMap

object DatabaseGenerator
{

  private def templateKeys(packageName: String, models: Array[String], migrations: Array[String], dataAccessObjects: Array[String]) = 
    ListMap[String, String](
      "IMPORT_MIGRATIONS_IF_ANY" -> (if (migrations.isEmpty) "" else "import PACKAGE_DB.migration._\n"),
      "MIGRATIONS_LIST" -> ("      " + (migrations mkString ",\n      ")),
      "NEW_DATABASE_SCHEMA" -> (dataAccessObjects map("applicationDB." + Util.uncapitalize(_) + ".ddl") mkString " ++ "),
      "TABLE_REPRESENTATION_INSTANCES" -> (dataAccessObjects map(dao => "  val " + Util.uncapitalize(dao) + " = TableQuery[" + dao + "]") mkString "\n"),
      "PACKAGE_NAME_AS_DIR" -> packageName.replace('.', '/'),
      "PACKAGE_NAME" -> packageName,
      "PACKAGE_UI" -> (packageName + ".ui"),
      "PACKAGE_R" -> (packageName + ".R"),
      "PACKAGE_DB" -> (packageName + ".db"),
      "PACKAGE_MODELS" -> (packageName + ".models")
    )

  private def templateMigrationTableKeys(packageName: String, table: Table, allTables: Array[Table]) =
    ListMap[String, String](
      "IMPORT_TABLE_FIELDS_DEPENDENCIES" -> resolveTableFieldsImports(table.fields),
      "INJECT_IMPLICITS_IF_NEEDED" -> implicitsForFields(table.fields),
      "TABLE_ROW_HELPERS" -> createTableRowHelpers(table, allTables),
      "MODEL_FIELDS_COMMA_SEPERATED" -> (table.fields.map(tableField => tableField.name + ": " + tableField.typeSimple).reduce(_ + ", " + _)),
      "TABLE_FIELDS_TUPLE" -> (table.fields map(getFieldName(_)) mkString ", "),
      "TABLE_ROW_FIELDS" ->  (table.fields map(field => getFieldName(field) + ": " + field.typeSimple) mkString ", "),
      "MODEL_NAME_AS_IS" -> table.name,
      "TABLE_NAME" -> (if (table.isJoin) table.name else (table.name + "s")),
      "MODEL_NAME_PLURAL" -> (if (table.isJoin) table.name else (table.name + "s")),
      "DEFS_OF_FIELDS" -> defsOfFields(table.fields drop(if (table.isJoin) 0 else 1)),
      "DEF_OF_ID" -> (if (table.isJoin) "" else defOfId(table.fields head)),
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

  private def createTableRowHelpers(table: Table, allTables: Array[Table]): String =
  {
    if (table.isJoin)
    {
      ""
    }
    else
    {
      // check all 1-1
      val oneToOneRelations = table.fields.foldLeft("")
      {
        (tableHelpers, field) =>
        {
          val helperMethod =
            if (field.foreignModel != null && field.belognsToModel)
            {
              val modelName = Util.uncapitalize(field.foreignModel.name)
              """  def %s(implicit session: Session): Option[%s] =
                |    App.%s.filter(_.%s === %sId).firstOption
                |
                |""".stripMargin
                .format(modelName, Util.capitalize(modelName + "Row"), modelName + "s", field.name, modelName)
            }
            else
            {
              ""
            }

          tableHelpers + helperMethod
        }
      }

      // check 1-n
      val oneToManyRelations = allTables.filter(_.isJoin == false).foldLeft("")
      {
        (tableHelpers, currentTable) =>
        {
          val helperMethod =
            currentTable.fields.find(field => field.foreignModel != null && field.foreignModel.name == table.name) match {
              case Some(field: TableField) =>
              {
                if (field.isArray == false)
                {
                  // doesn't belong here
                  ""
                }
                else
                {
                  val defName = field.foreignModel.fields.find(modelField => modelField.typeSimple == currentTable.name).get.name
                  """  def %s(implicit session: Session): Seq[%sRow] =
                    |    App.%s.filter(_.%s === %s).list
                    |
                    |""".stripMargin
                    .format(defName, currentTable.name, Util.uncapitalize(currentTable.name) + "s", Util.uncapitalize(table.name + "Id"), field.name)
                }
              }
              case _ => ""
            }

          tableHelpers + helperMethod
        }
      }
      // TODO: check n-n
      val manyToManyRelations = allTables.filter(_.isJoin).foldLeft("")
      {
        (tableHelpers, currentTable) =>
        {
          val helperMethod =
            currentTable.fields.find(field => field.foreignModel != null && field.foreignModel.name == table.name) match {
              case Some(field: TableField) =>
              {
                if (field.isArray == false)
                {
                  val tablesModel = field.foreignModel
                  val tablesModelName = Util.uncapitalize(tablesModel.name)

                  val otherModel = currentTable.fields.find(tableField => tableField.foreignModel.name != table.name).get.foreignModel
                  val otherModelName = Util.uncapitalize(otherModel.name)
                  
                  // maybe not the best way to do this, what if another field has id but is not the main id?
                  val otherField = allTables.find(table => table.name == otherModel.name).get.fields.find(tableField => isFieldNameAnId(tableField.name)).get

                  val defName = tablesModel.fields.find(modelField => modelField.typeSimple == otherModel.name).get.name
                  val tableJoin =  Util.uncapitalize(currentTable.name)
                  """  def %s(implicit session: Session): List[%sRow] =
                    |  {
                    |    val query = for
                    |      {
                    |        %s <- App.%s
                    |        %s <- App.%s if %s.%s === %s.%s
                    |        %s <- App.%s if %s.%s === %s.%s
                    |      } yield %s
                    |
                    |    query.list
                    |  }
                    |""".stripMargin
                    .format(
                      defName, otherModel.name,
                      tableJoin, tableJoin,
                      tablesModelName, tablesModelName + "s", tablesModelName, field.name, tableJoin, tablesModelName + "Id",
                      otherModelName, otherModelName + "s", otherModelName, otherField.name, tableJoin, otherModelName + "Id",
                      otherModelName)
                }
                else
                {
                  throw new Exception("Badly formed Tables and TableFields.")
                }
              }
              case _ => ""
            }

          tableHelpers + helperMethod
        }
      }

      (oneToOneRelations + oneToManyRelations + manyToManyRelations)
    }
  }

  private def resolveTableFieldsImports(fields: Array[TableField]): String =
  {
    fields.foldLeft("")
    {
      (imports, field) =>
      {
        imports + (field.typeSimple match {
          case "Date" => "\nimport java.util.Date"
          case _ => ""
        })
      }
    }
  }

  private def getFieldName(field: TableField): String =
  {
    if (field.foreignModel == null)
    {
      field.name
    }
    else
    {
      Util.uncapitalize(field.foreignModel.name) + "Id"
    }
  }

  private def defsOfFields(fields: Array[TableField]): String =
    fields.map(field =>
      {
        if (field.foreignModel != null)
        {
          val fieldNameWithoutId = Util.uncapitalize(field.foreignModel.name)

          val defId = "  def %sId = column[%s](\"%s\")"
            .format(fieldNameWithoutId, field.typeSimple, Util.camelToUnderscore(Util.uncapitalize(fieldNameWithoutId)).toUpperCase)

          val defFk = "\n  def %sFK = foreignKey(\"%s_FK\", %sId, App.%s)(_.%s)"
            .format(fieldNameWithoutId, Util.camelToUnderscore(fieldNameWithoutId).toUpperCase, fieldNameWithoutId, Util.uncapitalize(field.foreignModel.name + "s"), field.name)

          defId + defFk
        }
        else
        {
          "  def %s = column[%s](\"%s\")"
            .format(field.name, field.typeSimple, Util.camelToUnderscore(Util.uncapitalize(field.name)).toUpperCase) 
        }
      }
    ) mkString "\n\n"

  private def defOfId(field: TableField): String =
    "\n  def %s = column[Int](\"ID\", O.PrimaryKey, O.AutoInc)\n".format(field.name)

  def implicitsForFields(fields: Array[TableField]): String =
  {
    val implicits = for (field <- fields)
    yield {
      field.typeSimple match {
        case "Date" =>
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

  def migrations(packagePath: String): Array[String] =
  {
    val migrationFolder = new File(packagePath + "/db/migration/")

    if (migrationFolder.exists == false)
    {
      Array[String]()
    }
    else
    {
      migrationFolder.listFiles map (file => file.getName.replaceFirst("[.][^.]+$", ""))
    }
  }

  def dataAccessObjects(packagePath: String): Array[String] =
  {
    val folder = new File(packagePath + "/db/dao/")

    if (folder.exists == false)
    {
      Array[String]()
    }
    else
    {
      folder.listFiles map(file => file.getName.replaceFirst("[.][^.]+$", ""))
    }
  }

  private def simpleName(fullCanonicalName: String): String =
  {
    val lastDot = fullCanonicalName.lastIndexOf('.')
    val name =
      if (lastDot == -1)
      {
        fullCanonicalName
      }
      else
      {
        fullCanonicalName.substring(lastDot + 1, fullCanonicalName.length)
      }

    Util.capitalize(name)
  }

  def loadModels(classDirectory: File, packageName: String, externalDependencyClasspath: Seq[Attributed[File]]): Array[Model] =
  {
    val modelsPath = new File(classDirectory.toString + "/" + packageName.replace('.', '/') + "/models/")

    val externalJars: Array[URL] = externalDependencyClasspath.map(_.data.toURL).toArray

    val classLoader = new URLClassLoader(Array[URL](classDirectory.toURL) ++ externalJars)

    val modelsFile = modelsPath.listFiles() filterNot(_.getName contains('$'))

    if (modelsFile == null || modelsFile.isEmpty)
    {
      throw new Exception("Models folder not found or is empty." + modelsPath)
    }

    modelsFile.map(modelFile =>
      {
        val modelName = modelFile.getName.takeWhile(_ != '.')

        val modelClass = classLoader.loadClass(packageName + ".models." + modelName)

        val modelFields = modelClass.getDeclaredFields()

        new Model(modelClass.getSimpleName, modelFields.map(
          field =>
          {
            val fieldName = simpleName(field.toString.split(' ').takeRight(2)(0))
            if (field.getType.isArray)
            {
              ModelField(field.getName, fieldName.dropRight(2), true)
            }
            else
            {
              ModelField(field.getName, fieldName, false)
            }
          }
        ))
      }
    )
  }
  
  // TODO: Refactor
  def isFieldNameAnId(field: String) = 
  {
    val fieldUnderscored = Util.camelToUnderscore(field)
    fieldUnderscored == "id" || fieldUnderscored.startsWith("id_") || fieldUnderscored.endsWith("_id") || fieldUnderscored.endsWith("_i_d")
  }

  def modelsToTables(models: Array[Model]): Array[Table] =
  {
    // get model ids fields, add them if not found
    val tablesIdField =
      models.map(model =>
        {
          val modelIdField = model.fields.find(field => isFieldNameAnId(field.name)).getOrElse(ModelField(Util.uncapitalize(model.name) + "Id", "Int", false))
          val tableIdField = TableField(modelIdField.name, modelIdField.typeSimple, false, true, model)
          (model.name, tableIdField)
        }
      ).toMap

    val modelsWithoutIds = models.map(model => model.copy(fields = model.fields filterNot(field => field.name == tablesIdField(model.name).name)))

    val emptyTables: Array[Table] = models map(model => Table(model.name, Array[TableField](tablesIdField(model.name).copy(foreignModel = null))))

    modelsWithoutIds.foldLeft(emptyTables)
    {
      (tables, model) =>
      {
        model.fields.foldLeft(tables)
        {
          (tablesWithFields: Array[Table], field) =>
          {
            val tableOfModel = tablesWithFields.find(table => table.name == model.name).get

            // check 1-n or n-n
            if (models.exists(model => model.name == field.typeSimple) && field.isArray == true)
            {
              // check n-n
              if (tableOfModel.fields.exists(tableField => tableField.name == tablesIdField(field.typeSimple).name))
              {
                //throw new Exception("n-n on model:" + model.name + "field: " + field.name)
                val otherTables = tablesWithFields.filter(table => table.name != model.name)

                val otherModel = models.find(model => model.name == field.typeSimple).get

                val fieldA = tablesIdField(otherModel.name)
                val fieldB = tablesIdField(model.name)
                val newMiddleTable = Table(otherModel.name + "s" + model.name + "s", Array[TableField](fieldA, fieldB), true)

                // remove the extra field previously added when doing 1-n relationship
                val newTableOfModel = tableOfModel.copy(fields = tableOfModel.fields.filter(tableField => tableField.name != tablesIdField(otherModel.name).name))
                otherTables :+ newTableOfModel :+ newMiddleTable
              }
              // 1-n
              else
              {
                val targetTable = tablesWithFields.find(table => table.name == field.typeSimple).get
                val otherTables = tablesWithFields.filter(table => table.name != field.typeSimple)

                val targetTableFieldsWithoutThisField = targetTable.fields filterNot(_.name == tablesIdField(model.name).name)
                
                val previousFieldBelognsToModel = targetTableFieldsWithoutThisField.size < targetTable.fields.size

                val tableField = tablesIdField(model.name).copy(isArray = true).copy(belognsToModel = previousFieldBelognsToModel)
                otherTables :+ targetTable.copy(fields = targetTableFieldsWithoutThisField :+ tableField)
              }
            }
            // check 1-1
            else if (models.exists(model => model.name == field.typeSimple) && field.isArray == false)
            {
              val otherTables = tablesWithFields.filter(table => table.name != model.name)

              val previousFieldWithSameName = tableOfModel.fields.find(_.name == tablesIdField(field.typeSimple).name).getOrElse(tablesIdField(field.typeSimple))
              val tableOfModelThisField = tableOfModel.fields filterNot(_.name == tablesIdField(field.typeSimple).name)

              val tableField = previousFieldWithSameName.copy(belognsToModel = true)
              otherTables :+ tableOfModel.copy(fields = tableOfModelThisField :+ tableField)
            }
            else
            {
              val otherTables = tablesWithFields.filter(table => table.name != model.name)

              val tableField = TableField(field.name, field.typeSimple, false)
              otherTables :+ tableOfModel.copy(fields = tableOfModel.fields :+ tableField)
            }
          }
        }
      }
    }
  }

  def migrateTables(sourceDirectory: File, sourceManaged: File, classDirectory: File, externalDependencyClasspath: Seq[Attributed[File]]): Seq[File] =
  {
    val packageName = Android.findPackageName(sourceDirectory)

    val models = loadModels(classDirectory, packageName, externalDependencyClasspath)

    val tables = modelsToTables(models)

    val filesAndContent = Util.getResourceFiles("database-migration/")
    tables flatMap (table =>
      {
        val keys = templateMigrationTableKeys(packageName, table, tables)

        val resultingFiles = for ((filePath, fileContent) <- filesAndContent)
        yield {
          val finalFilePath = new File(sourceDirectory.getPath() + "/" + applyTemplate(keys, filePath))
          val finalFileContent = applyTemplate(keys, fileContent)

          IO.write(finalFilePath, finalFileContent)
          finalFilePath
        }
        resultingFiles.toList
      }
    )
  }

  def generate(sourceDirectory: File, sourceManaged: File, classDirectory: File): Seq[File] =
  {
    val packageName = Android.findPackageName(sourceDirectory)

    val packagePath = sourceDirectory.getPath + "/main/scala/" + packageName.replace('.', '/')

    val modelsFolder = packagePath + "/models/"

    val models = new File(modelsFolder).listFiles map (file => file.getName.replaceFirst("[.][^.]+$", ""))

    val templateKeysForGeneration = templateKeys(packageName, models, migrations(packagePath), dataAccessObjects(packagePath))
    
    val filesAndContent = Util.getResourceFiles("database/")

    val resultingFiles = for ((filePath, fileContent) <- filesAndContent)
    yield {
      val finalFilePath = new File(sourceManaged.getPath() + "/" + applyTemplate(templateKeysForGeneration, filePath))
      val finalFileContent = applyTemplate(templateKeysForGeneration, fileContent)

      IO.write(finalFilePath, finalFileContent)
      finalFilePath
    }
    resultingFiles.toList
  }

  case class ModelField(name: String, typeSimple: String, isArray: Boolean)

  case class TableField(name: String, typeSimple: String, isArray: Boolean, belognsToModel: Boolean = true, foreignModel: Model = null)

  case class Model(name: String, fields: Array[ModelField])

  case class Table(name: String, fields: Array[TableField], isJoin: Boolean = false)
}
