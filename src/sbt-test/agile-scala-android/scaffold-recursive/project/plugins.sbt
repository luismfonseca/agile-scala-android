addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.2.11")

{
  val pluginVersion = System.getProperty("plugin.version")
  if (pluginVersion == null)
  {
    throw new RuntimeException("""|The system property 'plugin.version' is not defined.
                                  |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
  }
  else {
    addSbtPlugin("pt.pimentelfonseca" % "agile-scala-android" % pluginVersion)
  }
}