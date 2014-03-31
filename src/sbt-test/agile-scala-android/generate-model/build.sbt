import com.typesafe.sbt.SbtStartScript

import AssemblyKeys._

import AgileAndroidKeys._

import android.Keys._

android.Plugin.androidBuild

defaultAgileAndroidSettings

seq(SbtStartScript.startScriptForClassesSettings: _*)

version := "0.1"

assemblySettings

jarName in assembly := "foo.jar"

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

