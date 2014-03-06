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
    "org.specs2"      %% "specs2-core"           % "2.3.10",
    "org.specs2"      %% "specs2-matcher-extra"  % "2.3.10",
    "org.specs2"      %% "specs2-gwt"            % "2.3.10",
    "org.specs2"      %% "specs2-html"           % "2.3.10",
    "org.specs2"      %% "specs2-form"           % "2.3.10",
    "org.specs2"      %% "specs2-scalacheck"     % "2.3.10",
    "org.specs2"      %% "specs2-mock"           % "2.3.10",
    "org.specs2"      %% "specs2-junit"          % "2.3.10"
  )
}

addCompilerPlugin("org.scala-lang.plugins" % "macro-paradise_2.10.3-RC1" % "2.0.0-SNAPSHOT")

scalacOptions ++= Seq("-Yrangepos", "-deprecation", "-unchecked", "-feature", "-language:_")

logBuffered := false

/** Console */
initialCommands in console in Test := "import org.specs2._"

