import AssemblyKeys._

assemblySettings

sbtPlugin := true

name := "agile-scala-android"

organization := "pt.pimentelfonseca.luis"

version := "0.1-SNAPSHOT"

//scalaVersion := "2.11.0-RC1"
//licenses := Seq("MIT License" -> url("https://github.com/sbt/sbt-assembly/blob/master/LICENSE"))
//libraryDependencies ++= Seq(
//  "org.scala-lang.modules" %% "scala-xml" % "1.0.0"
//)

addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.2.11")

scalacOptions ++= Seq("-deprecation", "-Xlint")