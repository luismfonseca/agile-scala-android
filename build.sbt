import AssemblyKeys._

import SonatypeKeys._

assemblySettings

sonatypeSettings

sbtPlugin := true

name := "agile-scala-android"

organization := "pt.pimentelfonseca"

profileName := "pt.pimentelfonseca"

version := "0.5"

//scalaVersion := "2.11.0"
//libraryDependencies ++= Seq(
//  "org.scala-lang" % "scala-library-all" % "2.11.0"
//)
scalacOptions ++= Seq("-deprecation", "-Xlint")

licenses := Seq("MIT License" -> url("https://raw.githubusercontent.com/luismfonseca/agile-scala-android/master/LICENSE"))

//libraryDependencies += Defaults.sbtPluginExtra(
//    m = "com.hanhuy.sbt" % "android-sdk-plugin" % "1.2.14", // Plugin module name and version
//    sbtV = "0.13",    // SBT version
//    scalaV = "2.10"    // Scala version compiled the plugin
//)

libraryDependencies += "com.google.code.gson" % "gson" % "2.2.4"

addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.2.16")

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

pomExtra := {
  <url>https://github.com/luismfonseca/agile-scala-android</url>
  <scm>
    <connection>scm:git:github.com/luismfonseca/agile-scala-android.git</connection>
    <developerConnection>scm:git:git@github.com:luismfonseca/agile-scala-android.git</developerConnection>
    <url>github.com/luismfonseca/agile-scala-android.git</url>
  </scm>
  <developers>
    <developer>
      <id>luismfonseca</id>
      <name>Luís Fonseca</name>
      <url>www.pimentelfonseca.pt</url>
    </developer>
  </developers>
}