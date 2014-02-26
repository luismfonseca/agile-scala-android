import sbt._
import Keys._
import complete.DefaultParsers._
import scala.collection.mutable
import scala.io.Source
import Def.Initialize
import java.io.{IOException, PrintWriter, FileOutputStream, File}
import java.security.MessageDigest

object Plugin extends Plugin {
  import AgileAndroidKeys._
  
  object AgileAndroidKeys {
	  val generate = inputKey[File]("Generates stuff.")
    
	
	  def generateTask: Initialize[InputTask[File]] = Def.inputTask {
      val args: Seq[String] = spaceDelimited("  className <attributes>").parsed

      var lines = Seq[String](
        "class " + args.head + "(" + args.tail.reduce(_ + ", " + _) + ") "
        + "{\n  \n}"
      )

	    val file = new File(args.head + ".scala")
	    IO.writeLines(file, lines, IO.utf8)
	    file
	  }

    // a group of settings ready to be added to a project
    val defaultAgileAndroidSettings : Seq[sbt.Def.Setting[_]] = Seq(
	    generate := generateTask.evaluated
    )
  }
}