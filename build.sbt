import AssemblyKeys._

assemblySettings

sbtPlugin := true

name := "agile-scala-android"

organization := "pt.pimentelfonseca.luis"

version := "0.1-SNAPSHOT"
//licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-assembly/blob/master/LICENSE"))

addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.2.11")