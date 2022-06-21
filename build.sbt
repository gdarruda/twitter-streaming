ThisBuild / scalaVersion := "3.1.2"
ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "streaming-twitter",
    libraryDependencies += "com.lihaoyi" %% "requests" % "0.7.1",
    libraryDependencies += ("org.apache.spark" %% "spark-sql" % "3.3.0").cross(CrossVersion.for3Use2_13),
    libraryDependencies += ("org.apache.spark" %% "spark-sql-kafka-0-10" % "3.3.0").cross(CrossVersion.for3Use2_13),
  )
