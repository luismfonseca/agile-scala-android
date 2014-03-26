// Imports
import AgileAndroidKeys._

import android.Keys._

// Load default definitions
android.Plugin.androidBuild

defaultAgileAndroidSettings

// Proguard configuration
proguardCache := Seq(
  ProguardCache("org.scaloid") % "org.scaloid",
  ProguardCache("slick") % "com.typesafe.slick"
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize", "-dontwarn **")

proguardOptions in Android += "-keep public class org.sqldroid.**"

// External library dependecies
resolvers += "Mandubian repository snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/"

libraryDependencies += "org.scaloid" %% "scaloid" % "3.2-8"

libraryDependencies += "com.google.code.gson" % "gson" % "2.2.4"

libraryDependencies += "com.typesafe.slick" %% "slick" % "2.0.1"

// Tasks dependecies
run <<= run in Android

install <<= install in Android

// Other settings
platformTarget in Android := "android-19"
