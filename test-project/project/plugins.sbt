logLevel := Level.Info

lazy val root = Project("plugins", file(".")).dependsOn(plugin)

lazy val plugin = file("../").getCanonicalFile.toURI

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")
