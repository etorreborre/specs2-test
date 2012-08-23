/** Project */
name := "specs2-test"

version := "1.12.2"

organization := "org.specs2"

scalaVersion := "2.9.2"

/** Shell */
shellPrompt := { state => System.getProperty("user.name") + "> " }

/** Dependencies */
resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "releases" at "http://oss.sonatype.org/content/repositories/releases")

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.9", 
  "org.scala-tools.testing" % "test-interface" % "0.5", 
  "org.specs2" %% "specs2-scalaz-core" % "6.0.1",
  "org.specs2" %% "specs2" % "1.12.2-SNAPSHOT",
  "org.hamcrest" % "hamcrest-all" % "1.1",
  "org.mockito" % "mockito-all" % "1.9.0",
  "junit" % "junit" % "4.7",
  "org.pegdown" % "pegdown" % "1.0.2",
  "org.specs2" % "classycle" % "1.4.1"
)

/** Compilation */
scalacOptions += "-deprecation"

maxErrors := 20

pollInterval := 1000

logBuffered := false

testOptions := Seq(Tests.Filter(s =>
  Seq("Spec", "Suite", "Unit", "all", "index").exists(s.endsWith(_)) &&
    s.startsWith("examples")))

/** Console */
initialCommands in console := "import org.specs2._"

