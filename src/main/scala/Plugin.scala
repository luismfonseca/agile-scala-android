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

    val checkPermissions = inputKey[Unit]("Checks for required Android permissions that are missing from the manifest.")

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

      Create.newProjectAndroid(streams.value.log, sbtVersion.value, version.value, args(0), args(1).toInt)

      Project.defaultSettings ++ defaultAgileAndroidSettings
    }

    def scaffoldTask: Initialize[InputTask[Unit]] = Def.inputTask {

      val model = Scaffold.findModels((classDirectory in Compile).value, sourceDirectory.value).parsed

      val dirs = Scaffold.scaffoldFromModel((classDirectory in Compile).value, sourceDirectory.value, sourceDirectory.value, (externalDependencyClasspath in Runtime).value, model(0))
      streams.value.log.info(dirs.toString)
    }

    def checkPermissionsTask: Initialize[InputTask[Unit]] = Def.inputTask {
      import android.Keys._

      streams.value.log.info("Checking missing Android permissions.")

      val classesJarPath = (classesJar in Android).value.getPath.toString

      val missingPermissions = Permissions.getMissingNeededPermissions(sourceDirectory.value, (sources in Compile).value, taskTemporaryDirectory.value, classesJarPath)

      streams.value.log.warn("The following permissions are missing and should be added:")
      streams.value.log.warn(missingPermissions mkString "\n")
    }

    def databaseGeneratorTask: Initialize[Task[Seq[File]]] = Def.task {

      streams.value.log.info("Generating database support classes.")
      DatabaseGenerator.generate(sourceDirectory.value, (sourceManaged in Compile).value, (classDirectory in Compile).value)
    }

    val defaultAgileAndroidSettings : Seq[sbt.Def.Setting[_]] = Seq(
	    generate := generateTask.evaluated,
      scaffold := scaffoldTask.evaluated,
      scaffold <<= scaffold dependsOn(compile in Compile),
      checkPermissions <<= checkPermissionsTask dependsOn(Keys.`package` in Compile),
      sourceGenerators in Compile <+= databaseGeneratorTask
    )

    // this should be added in ~/.sbt/0.13/npa.sbt
    val agileAndroidNewProjectTask : Seq[sbt.Def.Setting[_]] = Seq(
      npa := npaTask.evaluated
    )
  }
}