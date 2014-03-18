// Imports
import AgileAndroidKeys._

import android.Keys._

// Load default definitions
android.Plugin.androidBuild

defaultAgileAndroidSettings

// Proguard configuration
proguardCache := Seq(
  ProguardCache("org.scaloid") % "org.scaloid"
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize")

proguardOptions in Android ++= Seq("-dontwarn **")


// External library dependecies
resolvers += "Mandubian repository snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/"

libraryDependencies += "org.scaloid" %% "scaloid" % "3.2-8"

libraryDependencies += "com.google.code.gson" % "gson" % "2.2.4"

// Tasks dependecies
run <<= run in Android

install <<= install in Android

// Other settings
platformTarget in Android := "android-MIN_SDK_VERSION"
