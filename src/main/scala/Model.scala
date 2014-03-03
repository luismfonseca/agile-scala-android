import sbt._
import java.io._
import java.net._
import scala.xml._
import sbt.complete._

object Model {

  def getFilePath(sourceDirectory: File, scalaSourceDirectory: File, modelName: String) =
    new File(
      Android.getModelsPath(sourceDirectory, scalaSourceDirectory).getPath + "/" + modelName + ".scala"
    )
  	
  def generate(modelName: String, fields: Seq[String]) = {
    var lines = Seq[String](
        "class " + modelName + "(val " + fields.reduce(_ + ", val " + _.replace(":", ": ")) + ") ",
        "{",
        "",
        "}")
    lines
  }
}