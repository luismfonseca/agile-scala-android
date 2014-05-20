// Imports
import AgileAndroidKeys._

import android.Keys._

// Load default definitions
android.Plugin.androidBuild

defaultAgileAndroidSettings

scalariformSettings

// Proguard configuration
proguardCache in Android ++= Seq(
  ProguardCache("org.scaloid") % "org.scaloid",
  ProguardCache("slick") % "com.typesafe.slick"
)

proguardOptions in Android ++= Seq(
  "-dontobfuscate",
  "-dontoptimize",
  "-dontnote com.google.gson.internal.UnsafeAllocator",
  "-dontwarn javax.naming.InitialContext",
  "-dontwarn scala.slick.**",
  "-dontnote org.slf4j.**",
  "-dontnote scala.slick.**",
  "-keep class scala.slick.**",
  "-keep public class org.sqldroid.**",
  "-keep class scala.collection.Seq.**",
  "-keep class scala.concurrent.Future$.**",
  "-keep class scala.slick.driver.JdbcProfile$Implicits",
  "-keep class org.scaloid.*"
)

// External library dependecies
resolvers += "Mandubian repository snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/"

libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "3.3-8",
  "com.google.code.gson" % "gson" % "2.2.4",
  "com.typesafe.slick" %% "slick" % "2.0.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

// Tasks dependecies
run <<= (run in Android) dependsOn checkPermissions

install <<= (install in Android) dependsOn checkPermissions

// Other settings
scalacOptions ++= Seq("-deprecation", "-feature", "-Xlint")

platformTarget in Android := "android-MIN_SDK_VERSION"
