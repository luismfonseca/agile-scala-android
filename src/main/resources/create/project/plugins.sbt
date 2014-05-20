resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("pt.pimentelfonseca" % "agile-scala-android" % "PLUGIN_VERSION")

addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.2.16")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.5.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")