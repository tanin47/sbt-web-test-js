sbtPlugin := true

name := "sbt-web-test-js"

organization := "com.fongmun"

version := "1.0.0-SNAPSHOT"

publishMavenStyle := true

libraryDependencies ++= Seq(
  "com.github.detro" % "phantomjsdriver" % "1.2.0" withSources() withJavadoc()
)

