package agile.android

import sbt._
import Keys._
import complete.DefaultParsers._
import scala.collection.mutable
import scala.io.Source
import Def.Initialize
import java.io.{IOException, PrintWriter, FileOutputStream, File}
import java.security.MessageDigest

object Plugin extends Plugin
{
  import AgileAndroidKeys._
  
  object AgileAndroidKeys
  {
	  val generate = inputKey[File]("Generates stuff.")
    
    val npa = inputKey[Seq[Setting[_]]]("Generates a new project for android development with Scala.")

    val scaffold = inputKey[Unit]("Scaffolds stuff.")

	  def generateTask: Initialize[InputTask[File]] = Def.inputTask {
      val args = spaceDelimited(" className <attributes>").parsed
      val (modelName, modelAttributes) = (args.head, args.tail)

      val file = Model.getFilePath(sourceDirectory.value, (scalaSource in Compile).value, modelName)
      val content = Model.generate(sourceDirectory.value, modelName, modelAttributes)

	    IO.writeLines(file, content, IO.utf8)
	    file
	  }

    def npaTask: Initialize[InputTask[Seq[Setting[_]]]] = Def.inputTask {
      val args: Seq[String] = spaceDelimited("package minSdkVersion").parsed
      if (args.length < 2) {
        sys.error("Incorrect parameters.")
      }
      val packageName = args(0)
      val minSdkVersion = args(1).toInt

      val directories = Create.directoriesWith(packageName, minSdkVersion)

      val templateKeysNewProject = Create.templateKeys(sbtVersion.value, packageName, minSdkVersion)

      IO.write(Create.sbtBuildPropertiesFile, Create.applyTemplate(templateKeysNewProject, Create.sbtBuildPropertiesContent))
      IO.write(Create.sbtBuildFile, Create.sbtBuildContent)

      streams.value.log.info("Generated sbt build properties")

      IO.write(Create.androidManifestFile, Create.applyTemplate(templateKeysNewProject, Create.manifestXMLContent))
      streams.value.log.info("Generated Android manifest file.")

      IO.createDirectories(directories)
      streams.value.log.info("Generated source directories.")

      IO.write(Create.valuesStringFile, Create.valuesStringXMLContent)
      IO.write(Create.valuesDimensionsFile, Create.valuesDimensionsXMLContent)
      IO.write(Create.valuesStylesFile, Create.valuesStylesXMLContent)
      IO.write(Create.layoutMainFile, Create.layoutMainXMLContent)
      IO.write(Create.drawableHdpiFile, Create.drawableHdpiByteArray)
      IO.write(Create.drawableMdpiFile, Create.drawableMdpiByteArray)
      IO.write(Create.drawableXHdpiFile, Create.drawableXHdpiByteArray)
      // Todo: the main class
      streams.value.log.info("Generated source files.")

      IO.write(Create.gitignoreFile, Create.defaultGitIgnoreContent)
      streams.value.log.info("Generated .gitignore file.")

      //Project.addExtraBuilds(state.value, List[sbt.URI](new sbt.URI("build.sbt")))
      //Project.setExtraBuilds(state.value, List[sbt.URI](new sbt.URI("build.sbt")))
      //Project.updateCurrent(state.value)
      //Project.loadAction(state.value, Project.LoadAction.Plugins)
      streams.value.log.info("Please type 'reload' to allow sbt to load the new build definitions!")
      Project.defaultSettings ++ defaultAgileAndroidSettings
    }

    def scaffoldTask: Initialize[InputTask[Unit]] = Def.inputTask {

      val model = Scaffold.findModels((classDirectory in Compile).value, sourceDirectory.value).parsed

      val dirs = Scaffold.scaffoldFromModel((classDirectory in Compile).value, sourceDirectory.value, sourceDirectory.value, model(0))
      streams.value.log.info(dirs.toString)
    }


    val defaultAgileAndroidSettings : Seq[sbt.Def.Setting[_]] = Seq(
	    generate := generateTask.evaluated,
      scaffold := scaffoldTask.evaluated,
      scaffold <<= scaffold.dependsOn(compile in Compile)
    )

    // this should be added in ~/.sbt/0.13/npa.sbt
    val agileAndroidNewProjectTask : Seq[sbt.Def.Setting[_]] = Seq(
      npa := npaTask.evaluated
    )
  }
}