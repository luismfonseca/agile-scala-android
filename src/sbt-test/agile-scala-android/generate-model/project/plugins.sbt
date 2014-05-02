resolvers += Resolver.url("publishTo", new URL("https://repo.typesafe.com/typesafe/releases/"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-start-script" % "0.10.0")

{
  val pluginVersion = System.getProperty("plugin.version")
  if (pluginVersion == null)
  {
    throw new RuntimeException("""|The system property 'plugin.version' is not defined.
                                  |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
  }
  else {
    //addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.2")
    addSbtPlugin("pt.pimentelfonseca" % "agile-scala-android" % pluginVersion)
  }
}