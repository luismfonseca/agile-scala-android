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
	  val generate = inputKey[Seq[File]]("Generates stuff.")
    
    val npa = inputKey[Seq[Setting[_]]]("Generates a new project for android development with Scala.")

    val scaffold = inputKey[Unit]("Scaffolds stuff.")

	  def generateTask: Initialize[InputTask[Seq[File]]] = Def.inputTask {
      val args = spaceDelimited(" className <attributes>").parsed
      val (modelName, modelAttributes) = (args.head, args.tail)

      //val file = Model.getFilePath(sourceDirectory.value, (scalaSource in Compile).value, modelName)
      
      Model.generate(sourceDirectory.value, modelName, modelAttributes)
	  }

    def npaTask: Initialize[InputTask[Seq[Setting[_]]]] = Def.inputTask {
      val args: Seq[String] = spaceDelimited("package minSdkVersion").parsed
      if (args.length < 2) {
        sys.error("Incorrect parameters.")
      }
      val packageName = args(0)
      val minSdkVersion = args(1).toInt

      val directories = Create.directoriesWith(packageName, minSdkVersion)

      val templateKeysNewProject = Create.templateKeys(sbtVersion.value, version.value, packageName, minSdkVersion)

      IO.write(Create.sbtBuildPropertiesFile, Create.applyTemplate(templateKeysNewProject, Create.sbtBuildPropertiesContent))
      IO.write(Create.sbtBuildFile, Create.applyTemplate(templateKeysNewProject, Create.sbtBuildContent))
      IO.write(Create.sbtPluginsFile, Create.applyTemplate(templateKeysNewProject, Create.sbtPluginsContent))
      streams.value.log.info("Generated sbt build properties")

      IO.write(Create.androidManifestFile, Create.applyTemplate(templateKeysNewProject, Create.manifestXMLContent))
      streams.value.log.info("Generated Android manifest file.")

      IO.createDirectories(directories)
      streams.value.log.info("Generated source directories.")

      IO.write(Create.valuesStringFile, Create.valuesStringXMLContent)
      IO.write(Create.valuesDimensionsFile, Create.valuesDimensionsXMLContent)
      IO.write(Create.valuesStylesFile, Create.valuesStylesXMLContent)
      IO.write(Create.layoutMainFile, Create.layoutMainXMLContent)
      IO.write(
        new File(Create.applyTemplate(templateKeysNewProject, Create.mainActivityFile.getPath)),
        Create.applyTemplate(templateKeysNewProject, Create.mainActivityContent)
      )
      IO.write(Create.drawableHdpiFile, Create.drawableHdpiByteArray)
      IO.write(Create.drawableMdpiFile, Create.drawableMdpiByteArray)
      IO.write(Create.drawableXHdpiFile, Create.drawableXHdpiByteArray)
      streams.value.log.info("Generated source files.")

      IO.write(Create.gitignoreFile, Create.defaultGitIgnoreContent)
      streams.value.log.info("Generated .gitignore file.")

      //Project.addExtraBuilds(state.value, List[sbt.URI](new sbt.URI("build.sbt")))
      //Project.setExtraBuilds(state.value, List[sbt.URI](new sbt.URI("build.sbt")))
      //Project.updateCurrent(state.value)
      //Project.loadAction(state.value, Project.LoadAction.Plugins)
      streams.value.log.warn("Please type 'reload' to allow sbt to load the new build definitions!")
      Project.defaultSettings ++ defaultAgileAndroidSettings
    }

    def scaffoldTask: Initialize[InputTask[Unit]] = Def.inputTask {

      val model = Scaffold.findModels((classDirectory in Compile).value, sourceDirectory.value).parsed

      val dirs = Scaffold.scaffoldFromModel((classDirectory in Compile).value, sourceDirectory.value, sourceDirectory.value, (externalDependencyClasspath in Runtime).value, model(0))
      streams.value.log.info(dirs.toString)
    }

    def databaseGeneratorTask: Initialize[Task[Seq[File]]] = Def.task {

      streams.value.log.info("Generating database support classes.")
      DatabaseGenerator.generate(sourceDirectory.value, (sourceManaged in Compile).value, (classDirectory in Compile).value)
    }

    val defaultAgileAndroidSettings : Seq[sbt.Def.Setting[_]] = Seq(
	    generate := generateTask.evaluated,
      scaffold := scaffoldTask.evaluated,
      scaffold <<= scaffold dependsOn(compile in Compile),
      sourceGenerators in Compile <+= databaseGeneratorTask
    )

    // this should be added in ~/.sbt/0.13/npa.sbt
    val agileAndroidNewProjectTask : Seq[sbt.Def.Setting[_]] = Seq(
      npa := npaTask.evaluated
    )
  }
}