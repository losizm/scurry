organization  := "com.github.losizm"
name          := "scurry"
version       := "0.1.0"
versionScheme := Some("early-semver")
description   := "The Groovy-esque wrapper for Scamper"
homepage      := Some(url("https://github.com/losizm/scurry"))
licenses      := List("Apache License, Version 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalaVersion  := "3.1.3"
scalacOptions := Seq("-deprecation", "-feature", "-new-syntax", "-Werror", "-Yno-experimental")

Compile / doc / scalacOptions := Seq(
  "-project", name.value.capitalize,
  "-project-version", version.value,
  "-project-logo", "images/logo.svg"
)

libraryDependencies ++= Seq(
  "com.github.losizm" %% "scamper"     % "38.0.0" % "provided",
  "org.apache.groovy" %  "groovy"      % "4.0.15" % "test",
  "org.apache.groovy" %  "groovy-json" % "4.0.15" % "test",
  "org.scalatest"     %% "scalatest"   % "3.2.17" % "test"
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/losizm/scurry"),
    "scm:git@github.com:losizm/scurry.git"
  )
)

developers := List(
  Developer(
    id    = "losizm",
    name  = "Carlos Conyers",
    email = "carlos.conyers@hotmail.com",
    url   = url("https://github.com/losizm")
  )
)

publishMavenStyle := true

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org"
  isSnapshot.value match {
    case true  => Some("snaphsots" at s"$nexus/content/repositories/snapshots")
    case false => Some("releases"  at s"$nexus/service/local/staging/deploy/maven2")
  }
}
