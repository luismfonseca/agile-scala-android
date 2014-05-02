resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies <+= sbtVersion ("org.scala-sbt" % "scripted-plugin" % _)

//libraryDependencies <+= (sbtVersion) { sv =>
//  "org.scala-sbt" % "scripted-plugin" % sv
//}

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.2")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.4.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "0.2.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-pgp" % "0.8.2")