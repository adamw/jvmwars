lazy val commonSettings = commonSmlBuildSettings ++ ossPublishSettings ++ Seq(
  organization := "com.softwaremill.jvmwars",
  scalaVersion := "2.13.3"
)

val scalaTest = "org.scalatest" %% "scalatest" % "3.2.2" % Test

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(publishArtifact := false, name := "root")
  .aggregate(core)

lazy val core: Project = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "1.0.1",
      "com.softwaremill.sttp.client3" %% "httpclient-backend-zio" % "3.0.0-RC5",
      "com.softwaremill.sttp.client3" %% "circe" % "3.0.0-RC5",
      "io.circe" %% "circe-generic-extras" % "0.13.0",
      scalaTest
    )
  )
