//resolvers += Resolver.url("publishTo", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

//resolvers += Resolver.url("publishTo", new URL("https://repo.typesafe.com/typesafe/releases/"))(Resolver.ivyStylePatterns)
resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases"

//libraryDependencies <+= sbtVersion ("org.scala-sbt" % "scripted-plugin" % _)

libraryDependencies <+= (sbtVersion) { sv =>
  "org.scala-sbt" % "scripted-plugin" % sv
}

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.2")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.4.0")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"