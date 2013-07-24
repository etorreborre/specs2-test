/** Project */
name := "specs2-test"

organization := "org.specs2"

scalaVersion := "2.10.2"

/** Shell */
shellPrompt := { state => System.getProperty("user.name") + "> " }

/** Dependencies */
resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "releases" at "http://oss.sonatype.org/content/repositories/releases")

libraryDependencies <++= version { version =>
  Seq(
    "org.specs2"      %% "specs2"       % version,
    "org.scala-lang"  % "scala-reflect" % "2.10.2",
    "com.chuusai"     %% "shapeless"    % "1.2.4", 
    "org.scalacheck"  %% "scalacheck"   % "1.10.0", 
    "org.hamcrest"    % "hamcrest-all"  % "1.1",
    "org.mockito"     % "mockito-all"   % "1.9.0",
    "junit"           % "junit"         % "4.11",
    "org.pegdown"     % "pegdown"       % "1.2.1",
    "org.specs2"      % "classycle"     % "1.4.1" 
  )
}

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-language:implicitConversions,reflectiveCalls,postfixOps,higherKinds,existentials,experimental.macros")

scalacOptions in Test ++= Seq("-Yrangepos")

logBuffered := false

/** Console */
initialCommands in console := "import org.specs2._"

