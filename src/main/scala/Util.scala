import scala.xml.Node
import scala.xml.PrettyPrinter
import sbt.IO
import java.io.File

object Util
{

  def uncapitalize(value: String) =
    value(0).toString().toLowerCase() + value.tail

  def camelToUnderscore(value: String) =
    "[A-Z\\d]".r.replaceAllIn(value, "_" + _.group(0).toLowerCase())

  def underscoreToCamel(value: String) =
    "_([a-z\\d])".r.replaceAllIn(value, _.group(1).toUpperCase())



  def saveXML(file: File, node: Node) = {

    val content = Seq(
      "<?xml version='1.0' encoding='UTF-8'?>" + IO.Newline,
      new PrettyPrinter(1200, 4).format(node).trim)

    IO.writeLines(file, content, IO.utf8)
  }

}