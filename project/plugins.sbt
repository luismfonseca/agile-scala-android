//resolvers += Resolver.url("publishTo", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

//resolvers += Resolver.url("publishTo", new URL("https://repo.typesafe.com/typesafe/releases/"))(Resolver.ivyStylePatterns)
resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases"

//libraryDependencies <+= sbtVersion ("org.scala-sbt" % "scripted-plugin" % _)

libraryDependencies <+= (sbtVersion) { sv =>
  "org.scala-sbt" % "scripted-plugin" % sv
}