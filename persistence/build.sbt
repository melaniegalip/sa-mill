name := "mill-persistence"
scalaVersion := "3.3.3"
val scalafxVersion = "18.0.1-R28"

libraryDependencies ++= Seq(
  ("com.typesafe.play" %% "play-json" % "2.8.2")
    .cross(CrossVersion.for3Use2_13),
  ("org.scala-lang.modules" %% "scala-xml" % "2.0.1"),
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.0",
  "com.typesafe.akka" %% "akka-stream" % "2.8.0",
  "com.typesafe.akka" %% "akka-http" % "10.5.1",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.slick" %% "slick" % "3.5.1",
  "org.postgresql" % "postgresql" % "42.6.0",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  ("com.typesafe.play" %% "play-json" % "2.8.2")
      .cross(CrossVersion.for3Use2_13),
  ("org.mongodb.scala" %% "mongo-scala-driver" % "4.8.0")
    .cross(CrossVersion.for3Use2_13),
  "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
  ("net.codingwell" %% "scala-guice" % "5.0.2")
    .cross(CrossVersion.for3Use2_13)
)