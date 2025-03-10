name := """api"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.16"

libraryDependencies += guice

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
  "mysql" % "mysql-connector-java" % "8.0.19"
)

libraryDependencies += "com.typesafe.play" %% "filters-helpers" % "2.8.21"

libraryDependencies += "com.auth0" % "java-jwt" % "4.4.0"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test

dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.2.0"

libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"

libraryDependencies += ws

libraryDependencies += "redis.clients" % "jedis" % "4.4.3"