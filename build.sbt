val scala3Version = "3.1.3"
val scalafxVersion = "18.0.1-R28"


ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := scala3Version

Compile / mainClass := Some("de.htwg.se.mill.Mill")
mainClass in (Compile, packageBin) := Some("de.htwg.se.mill.Mill")

name := "Mill"
scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-encoding",
  "utf8",
  "-feature"
)
libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.13",
  "org.scalatest" %% "scalatest" % "3.2.13" % "test",
  "org.scalafx" %% "scalafx" % scalafxVersion,
  "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
  ("net.codingwell" %% "scala-guice" % "5.0.2").cross(CrossVersion.for3Use2_13),
  ("com.typesafe.play" %% "play-json" % "2.8.2")
    .cross(CrossVersion.for3Use2_13),
  ("org.scala-lang.modules" %% "scala-xml" % "2.0.1")
)

jacocoReportSettings := JacocoReportSettings(
  "Jacoco Coverage Report",
  None,
  JacocoThresholds(),
  Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML),
  "utf-8"
)
jacocoExcludes := Seq(
  "de.htwg.se.mill.Mill*",
  "de.htwg.se.mill.util*",
  "de.htwg.se.mill.aview.gui*"
)


