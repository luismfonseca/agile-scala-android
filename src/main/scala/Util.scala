package agile.android

import scala.xml.Node
import scala.xml.XML
import scala.xml.PrettyPrinter
import sbt.IO
import java.util.Scanner
import java.util.jar.{JarFile, JarEntry}
import java.io.{File, InputStream}
import collection.JavaConversions.enumerationAsScalaIterator
import scala.tools.nsc.io.Streamable

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

  def convertInputStreamToByteArray(inputStream: InputStream) =
    Iterator.continually(inputStream.read).takeWhile(-1 !=).map(_.toByte).toArray

  def mergeChildrenXML(a: xml.Elem, b: xml.Elem, attribute: String, overriding: Boolean) =
  {
    a.child ++
    (
      if (overriding)
      {
        b.child filterNot a.contains
      }
      else
      {
        if (attribute contains ":")
        {
          val attributeName = (attribute split ":")(1)
          b.child filterNot(elementb => a.child.exists(elementa =>
            (elementa.attribute("http://schemas.android.com/apk/res/android", "name") == elementb.attribute("http://schemas.android.com/apk/res/android", "name"))))
        }
        else
        {
          b.child filterNot(elementb => a.child.exists(elementa => (elementa.attribute(attribute) == elementb.attribute(attribute))))
        }
      }
    )
  }

  def mergeXML(a: xml.Elem, b: xml.Elem, attribute: String, overriding: Boolean) =
    XML.loadString("<" + a.label + ">\n    " + (mergeChildrenXML(a, b, attribute, overriding) filterNot(node => node.text.trim == "") mkString("\n    ")) + "\n</" + a.label + ">")

  def appendNodesXML(a: xml.Node, b: xml.Node) =
  {
    a.asInstanceOf[scala.xml.Elem].copy(child = a.child :+ b)
  }

  def prettyXML(node: Node) = Seq(
    "<?xml version='1.0' encoding='UTF-8'?>" + IO.Newline,
    new PrettyPrinter(120, 4).format(node).trim
  )


  def getResourceFilesRaw(path: String): Map[String, Array[Byte]] = 
  {

    val jarFile = new JarFile(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()))

    val files = jarFile.entries().foldLeft(Map[String, Array[Byte]]()) {
      (resultingList, entry) => {
        if (entry.getName().startsWith(path) && entry.getName() != path && entry.getName().endsWith("/") == false)
        {
          val fileContent = convertInputStreamToByteArray(getClass.getClassLoader().getResourceAsStream(entry.getName()))

          resultingList ++ Map((entry.getName().stripPrefix(path), fileContent))
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