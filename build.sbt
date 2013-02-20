/** Project */
name := "specs2-test"

organization := "org.specs2"

scalaVersion := "2.10.0"

/** Shell */
shellPrompt := { state => System.getProperty("user.name") + "> " }

/** Dependencies */
resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "releases" at "http://oss.sonatype.org/content/repositories/releases")

libraryDependencies <++= version { version =>
  Seq(
    "org.specs2" %% "specs2" % version,
    "org.scalacheck" %% "scalacheck" % "1.10.0", 
    "org.scala-tools.testing" % "test-interface" % "0.5", 
    "org.hamcrest" % "hamcrest-all" % "1.1",
    "org.mockito" % "mockito-all" % "1.9.0",
    "junit" % "junit" % "4.7",
    "org.pegdown" % "pegdown" % "1.0.2",
    "org.specs2" % "classycle" % "1.4.1" 
  )
}

logBuffered := false

/** Console */
initialCommands in console := "import org.specs2._"

