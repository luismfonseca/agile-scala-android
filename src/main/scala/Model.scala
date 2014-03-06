import sbt._
import java.io._
import java.net._
import scala.xml._
import sbt.complete._
import scala.collection.immutable.HashMap

object Model
{
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

  def getFilePath(sourceDirectory: File, scalaSourceDirectory: File, modelName: String) =
    new File(
      Android.getModelsPath(sourceDirectory, scalaSourceDirectory).getPath + "/" + modelName + ".scala"
    )
    
  // TODO: accept only known fields
  def generate(sourceDirectory: File, modelName: String, fields: Seq[String]) = {

    val packageName = "package " + Android.findPackageName(sourceDirectory) + ".models"

    val imports = resolveImports(fields.map(s => { s.split(":")(1).split("\\[")(0) }))

    var lines = Seq[String](
      packageName,
      "",
      imports,
        "case class " + modelName + "(" + fields.reduce(_ + ", " + _.replace(":", ": ")) + ")",
        "{",
        "  ",
        "}")
    lines
  }
}