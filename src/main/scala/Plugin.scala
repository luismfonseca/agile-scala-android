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
    
    val npa = inputKey[Unit]("Generates a new project for android development with Scala.")
	
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

    def npaTask: Initialize[InputTask[Unit]] = Def.inputTask {
      val args: Seq[String] = spaceDelimited("package minApiLevel").parsed
      if (args.length < 2) {
        sys.error("Incorrect parameters.")
      }
      val packageName = args(0)
      val minApiLevel = args(1).toInt

      val directories = Create.directoriesWith(packageName, minApiLevel)

      IO.write(Create.sbtBuildPropertiesFile, Create.sbtBuildPropertiesContent(sbtVersion.value))
      IO.write(Create.sbtBuildFile, Create.sbtBuildContent)

      streams.value.log.info("Generated sbt build properties")

      IO.write(Create.androidManifestFile, Create.manifestXML(packageName, minApiLevel))
      streams.value.log.info("Generated Android manifest file.")

      IO.createDirectories(directories)
      streams.value.log.info("Generated source directories.")

      IO.write(Create.valuesStringFile, Create.valuesStringXML)
      IO.write(Create.layoutMainFile, Create.layoutMainXML)
      // Todo: the main class
      streams.value.log.info("Generated source files.")

      IO.write(Create.gitignoreFile, Create.defaultGitIgnore)
      streams.value.log.info("Generated .gitignore file.")

      //Project.addExtraBuilds(state.value, List[sbt.URI](new sbt.URI("build.sbt")))
      //Project.setExtraBuilds(state.value, List[sbt.URI](new sbt.URI("build.sbt")))
      //Project.updateCurrent(state.value)
      Project.loadAction(state.value, Project.LoadAction.Plugins)
    }

    val defaultAgileAndroidSettings : Seq[sbt.Def.Setting[_]] = Seq(
	    generate := generateTask.evaluated,
      npa := npaTask.evaluated
    )

    // this should be added in ~/.sbt/0.13/npa.sbt
    val AgileAndroidNewProjectTask : Seq[sbt.Def.Setting[_]] = Seq(
      npa := npaTask.evaluated
    )
  }
}