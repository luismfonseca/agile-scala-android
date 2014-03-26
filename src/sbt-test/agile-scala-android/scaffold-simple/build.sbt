import com.typesafe.sbt.SbtStartScript

import AssemblyKeys._

import android.Keys._

import AgileAndroidKeys._

defaultAgileAndroidSettings

android.Plugin.androidBuild

seq(SbtStartScript.startScriptForClassesSettings: _*)

proguardCache := Seq(
  ProguardCache("org.scaloid") % "org.scaloid",
  ProguardCache("slick") % "com.typesafe.slick"
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize")

proguardOptions in Android ++= Seq("-dontwarn **")

proguardOptions in Android += "-keep public class org.sqldroid.**"

version := "0.1"

assemblySettings

jarName in assembly := "foo.jar"

resolvers += "Mandubian repository snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/"

libraryDependencies += "org.scaloid" %% "scaloid" % "3.2-8"

libraryDependencies += "com.google.code.gson" % "gson" % "2.2.4"

libraryDependencies += "com.typesafe.slick" %% "slick" % "2.0.1"

//libraryDependencies += "org.sqldroid" %% "sqldroid" % "1.0.0.RC11-SNAPSHOT"

// Tasks dependecies
run <<= run in Android

install <<= install in Android

// Other settings
platformTarget in Android := "android-19"

InputKey[Unit]("contents") <<= inputTask { (argsTask: TaskKey[Seq[String]]) =>
  (argsTask, streams) map {
    (args, out) =>
      args match {
        case Seq(given, expected) =>
          if(IO.readLines(file(given)).equals(IO.readLines(file(expected)))) out.log.debug(
            "Contents match"
          )
          else error(
            "Contents of (%s)\n%s does not match (%s)\n%s" format(
              given, IO.read(file(given)), expected, IO.read(file(expected))
            )
          )
      }
  }
}

TaskKey[Unit]("check") <<= (crossTarget) map { (crossTarget) =>
  val process = sbt.Process("java", Seq("-jar", (crossTarget / "foo.jar").toString))
  val out = (process!!)
  if (out.trim != "hello") error("unexpected output: " + out)
  ()
}

