name := "icecast-stat"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies += "org.scala-lang" % "scala-xml" % "2.11.0-M4"
libraryDependencies += "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23"
libraryDependencies += "com.typesafe" % "config" % "1.3.0"
libraryDependencies += "org.apache.logging.log4j" % "log4j" % "2.3"

import com.github.retronym.SbtOneJar._

oneJarSettings

mainClass in oneJar := Some("hu.tilos.radio.stat.Cli")