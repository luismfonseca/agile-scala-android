import scala.xml.Node
import scala.xml.PrettyPrinter
import sbt.IO
import java.util.Scanner
import java.util.jar.{JarFile, JarEntry}
import java.io.{File, InputStream}
import collection.JavaConversions.enumerationAsScalaIterator

object Util
{
  def capitalize(value: String) =
    value(0).toString().toUpperCase() + value.tail

  def uncapitalize(value: String) =
    value(0).toString().toLowerCase() + value.tail

  def camelToUnderscore(value: String) =
    "[A-Z\\d]".r.replaceAllIn(value, "_" + _.group(0).toLowerCase())

  def underscoreToCamel(value: String) =
    "_([a-z\\d])".r.replaceAllIn(value, _.group(1).toUpperCase())

  def camelToSpace(value: String) =
    capitalize("[A-Z\\d]".r.replaceAllIn(value, " " + _.group(0)))

  def convertStreamToString(inputStream: InputStream): String = {
    val scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A")
    if (scanner.hasNext()) scanner.next() else ""
  }

  def convertInputStreamToArray(inputStream: InputStream) = {
    Stream.continually(inputStream.read).takeWhile(-1 !=).map(_.toByte).toArray
  }

  def saveXML(file: File, node: Node) = {

    val content = Seq(
      "<?xml version='1.0' encoding='UTF-8'?>" + IO.Newline,
      new PrettyPrinter(1200, 4).format(node).trim)

    IO.writeLines(file, content, IO.utf8)
  }

  def getResourceFiles(path: String): Map[String, String] =
  {
    val jarFile = new JarFile(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()))

    val files = jarFile.entries().foldLeft(Map[String, String]()) {
      (resultingList, entry) => {
        if (entry.getName().startsWith(path))
        {
          val fileContent = convertStreamToString(getClass.getClassLoader().getResourceAsStream(entry.getName()))
          if (fileContent.isEmpty() == false)
          {
            resultingList ++ Map((entry.getName().stripPrefix(path), fileContent))
          }
          else
          {
            resultingList
          }
        }
        else
        {
          resultingList
        }
      }
    }

    jarFile.close()

    files
  }

}