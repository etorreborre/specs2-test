/** Project */
name := "specs2-test"

organization := "org.specs2"

scalaVersion := "2.10.3"

/** Shell */
shellPrompt := { state => System.getProperty("user.name") + "> " }

/** Dependencies */
resolvers ++= Seq(Resolver.sonatypeRepo("snapshots"),
                  Resolver.sonatypeRepo("releases"))

libraryDependencies <++= version { version =>
  Seq(
    "org.specs2"      %% "specs2"           % "2.3",
    "org.scala-lang"  %  "scala-reflect"    % "2.10.3",
    "com.chuusai"     %  "shapeless_2.10.2" % "2.0.0-M1", 
    "org.scalacheck"  %% "scalacheck"       % "1.10.0", 
    "org.hamcrest"    %  "hamcrest-core"    % "1.3",
    "org.mockito"     %  "mockito-core"     % "1.9.5",
    "junit"           %  "junit"            % "4.11",
    "org.pegdown"     %  "pegdown"          % "1.2.1",
    "org.specs2"      %  "classycle"        % "1.4.1" 
  )
}

addCompilerPlugin("org.scala-lang.plugins" % "macro-paradise_2.10.3-RC1" % "2.0.0-SNAPSHOT")

scalacOptions ++= Seq("-Yrangepos", "-deprecation", "-unchecked", "-feature", "-language:_")

logBuffered := false

/** Console */
initialCommands in console in Test := "import org.specs2._"

