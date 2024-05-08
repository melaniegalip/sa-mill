import com.typesafe.sbt.packager.Keys.{dockerBaseImage, dockerExposedPorts}
import com.typesafe.sbt.packager.docker._

val scala3Version = "3.2.2"
val scalafxVersion = "17.0.1-R26"
val playJsonVersion = "2.10.4"
val akkaHttp = "10.5.0"
val akkaActor = "2.8.0"

lazy val commonLibraries = Seq(
  "org.scalactic" %% "scalactic" % "3.2.17",
  "org.scalatest" %% "scalatest" % "3.2.17" % "test",
  "ch.qos.logback" % "logback-classic" % "1.3.14"
)

lazy val commonSettings = Seq(
  ThisBuild / version := "0.1.0-SNAPSHOT",
  ThisBuild / scalaVersion := scala3Version,
  ThisBuild / versionScheme := Some("early-semver"),
  ThisBuild / fork := true,
  jacocoReportSettings := JacocoReportSettings(
    "Jacoco Coverage Report",
    None,
    JacocoThresholds(),
    Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML),
    "utf-8"
  ),
  jacocoExcludes := Seq(
    "de.htwg.se.mill.Mill*",
    "de.htwg.se.mill.util*",
    "de.htwg.se.mill.aview.gui*"
  )
)

lazy val root = project
  .in(file("."))
  .dependsOn(aview, controller, model, persistence)
  .aggregate(util, model, aview, controller, persistence)
  .settings(
    name := "Mill",
    version := "0.1.0-SNAPSHOT",
    dockerBaseImage := "nicolabeghin/liberica-openjdk-with-javafx-debian:17",
    dockerExposedPorts := Seq(8080),
    dockerCommands ++= Seq(
      Cmd("USER", "root"),
      ExecCmd("RUN", "apt", "update"),
      ExecCmd(
        "RUN",
        "apt",
        "install",
        "-y",
        "--no-install-recommends",
        "mesa-utils",
        "libgl1-mesa-glx",
        "libgl1-mesa-dri",
        "libgl1-mesa-dev",
        "libxrender1",
        "libxtst6",
        "libxi6",
        "libgtk-3-0"
      )
    ),
    commonSettings,
    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % scalafxVersion,
      "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
      ("net.codingwell" %% "scala-guice" % "5.0.2")
        .cross(CrossVersion.for3Use2_13)
    ) ++ commonLibraries
  )
  .enablePlugins(JavaAppPackaging)

lazy val model = project
  .dependsOn(util)
  .settings(
    name := "Mill-model",
    commonSettings,
    libraryDependencies ++= Seq(
      ("com.typesafe.play" %% "play-json" % playJsonVersion)
        .cross(CrossVersion.for3Use2_13),
      ("org.scala-lang.modules" %% "scala-xml" % "2.1.0")
    ) ++ commonLibraries
  )

lazy val util = project
  .settings(
    name := "Mill-util",
    commonSettings
  )

lazy val controller = project
  .dependsOn(util, model)
  .settings(
    name := "Mill-controller",
    commonSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaActor,
      "com.typesafe.akka" %% "akka-stream" % akkaActor,
      "com.typesafe.akka" %% "akka-http" % akkaHttp,
      ("com.typesafe.play" %% "play-json" % playJsonVersion)
        .cross(CrossVersion.for3Use2_13),
      ("org.scala-lang.modules" %% "scala-xml" % "2.1.0"),
      "org.scalafx" %% "scalafx" % scalafxVersion,
      "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
      ("net.codingwell" %% "scala-guice" % "5.0.2")
        .cross(CrossVersion.for3Use2_13)
    ) ++ commonLibraries
  )

lazy val aview = project
  .dependsOn(controller)
  .settings(
    name := "Mill-aview",
    commonSettings,
    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % scalafxVersion
    ) ++ commonLibraries
  )

lazy val persistence = project
  .settings(
    name := "Mill-persistence",
    commonSettings,
    version := "0.1.0-SNAPSHOT",
    dockerBaseImage := "openjdk:17-jdk",
    dockerExposedPorts := Seq(8081),
    libraryDependencies ++= Seq(
      ("org.scala-lang.modules" %% "scala-xml" % "2.1.0"),
      "com.typesafe.akka" %% "akka-actor-typed" % akkaActor,
      "com.typesafe.akka" %% "akka-stream" % akkaActor,
      "com.typesafe.akka" %% "akka-http" % akkaHttp,
      ("com.typesafe.slick" %% "slick" % "3.5.1")
        .cross(CrossVersion.for3Use2_13),
      "org.postgresql" % "postgresql" % "42.5.0",
      ("com.typesafe.play" %% "play-json" % playJsonVersion)
        .cross(CrossVersion.for3Use2_13),
      ("org.mongodb.scala" %% "mongo-scala-driver" % "4.8.0")
        .cross(CrossVersion.for3Use2_13),
      "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
      ("net.codingwell" %% "scala-guice" % "5.0.2")
        .cross(CrossVersion.for3Use2_13)
    ) ++ commonLibraries
  )
  .enablePlugins(JavaAppPackaging)
