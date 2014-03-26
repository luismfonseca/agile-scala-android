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

  private def templateKeys(packageName: String, models: Array[String], migrations: Array[String], dataAccessObjects: Array[String]): ListMap[String, String] = 
    ListMap[String, String](
      "IMPORT_MIGRATIONS_IF_ANY" -> (if (migrations.isEmpty) "" else "import PACKAGE_DB.migration._\n"),
      "MIGRATIONS_LIST" -> ("      " + (migrations mkString ",\n      ")),
      "NEW_DATABASE_SCHEMA" -> (dataAccessObjects map("applicationDB." + Util.uncapitalize(_) + ".ddl") mkString " ++ "),
      "TABLE_REPRESENTATION_INSTANCES" -> (dataAccessObjects map(dao => "  val " + Util.uncapitalize(dao) + " = TableQuery[" + dao + "]") mkString "\n"),
      "PACKAGE_NAME_AS_DIR" -> packageName.replace('.', '/'),
      "PACKAGE_UI" -> (packageName + ".ui"),
      "PACKAGE_R" -> (packageName + ".R"),
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
      folder.listFiles map (file => file.getName.replaceFirst("[.][^.]+$", ""))
    }
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
}
