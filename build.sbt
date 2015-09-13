sbtPlugin := true

name := "sbt-web-test-js"

description := "A sbt-web plugin that run tests with Jasmine. Tests can be written in both Javascripts and Coffeescripts."

organization := "com.fongmun"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.10.4"

publishMavenStyle := true

libraryDependencies ++= Seq(
  "com.github.detro" % "phantomjsdriver" % "1.2.0" withSources() withJavadoc()
)

// add scala-xml dependency when needed (for Scala 2.11 and newer) in a robust way
// this mechanism supports cross-version publishing
// taken from: http://github.com/scala/scala-module-dependency-sample
libraryDependencies := {
  CrossVersion.partialVersion(scalaVersion.value) match {
    // if scala 2.11+ is used, add dependency on scala-xml module
    case Some((2, scalaMajor)) if scalaMajor >= 11 =>
      libraryDependencies.value ++ Seq(
        "org.scala-lang.modules" %% "scala-xml" % "1.0.3",
        "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3",
        "org.scala-lang.modules" %% "scala-swing" % "1.0.1")
    case _ =>
      // or just libraryDependencies.value if you don't depend on scala-swing
      libraryDependencies.value :+ "org.scala-lang" % "scala-swing" % scalaVersion.value
  }
}

licenses := Seq("The Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("http://www.github.com/tanin47/sbt-web-test-js"))

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

crossScalaVersions := Seq("2.10.4")

scmInfo := Some(ScmInfo(
  browseUrl = url("http://www.github.com/tanin47/sbt-web-test-js"),
  connection = "scm:git:git@github.com:tanin47/sbt-web-test-js.git",
  devConnection = Some("scm:git:git@github.com:tanin47/sbt-web-test-js.git")
))

pomIncludeRepository := { _ => false }

useGpg := true

pomExtra :=
  <developers>
    <developer>
      <name>Tanin Na Nakorn</name>
      <email>tanin47@yahoo.com</email>
      <url>http://www.github.com/tanin47</url>
    </developer>
  </developers>



