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
    
    val create = inputKey[Unit]("Generates a new android project.")
	
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

    def createTask: Initialize[InputTask[Unit]] = Def.inputTask {
      val args: Seq[String] = spaceDelimited("package minApiLevel").parsed
      if (args.length < 2) {
        sys.error("Incorrect parameters.")
      }
      val packageName = args(0)
      val minApiLevel = args(1).toInt

      val directories = Create.directoriesWith(packageName, minApiLevel)

      //IO.write(buildProps, "sbt.version=%s\n" format sbtVersion.value)
      //streams.value.log.info("Generated build properties")

      IO.write(Create.androidManifestFile, Create.manifestXML(packageName, minApiLevel))
      streams.value.log.info("Generated manifest file.")

      IO.createDirectories(directories)
      streams.value.log.info("Generated source directories.")

      IO.write(Create.valuesStringFile, Create.valuesStringXML)
      IO.write(Create.layoutMainFile, Create.layoutMainXML)
      // Todo: class
      streams.value.log.info("Generated source files.")

      IO.write(Create.gitignoreFile, Create.defaultGitIgnore)
      streams.value.log.info("Generated gitignore.")
    }

    // a group of settings ready to be added to a project
    val defaultAgileAndroidSettings : Seq[sbt.Def.Setting[_]] = Seq(
	    generate := generateTask.evaluated,
      create := createTask.evaluated
    )
  }
}