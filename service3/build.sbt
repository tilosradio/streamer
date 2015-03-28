organization := "hu.tilos.service3"

version := "0.1"

scalaVersion := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.2"
  Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-json" % "1.3.1",

    "org.scaldi" %% "scaldi-akka" % "0.5.3",
    "org.scaldi" %% "scaldi" % "0.5.3",
    "com.nimbusds" % "nimbus-jose-jwt" % "3.9.2",
    "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "io.spray" %% "spray-testkit" % sprayV % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.specs2" %% "specs2-core" % "2.3.11" % "test"
  )
}

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"
mainClass in Revolver.reStart := Some("hu.tilos.service3.Boot")

Revolver.settings

Revolver.enableDebugging(port = 5050, suspend = false)

