/** Project */
name := "specs2-test"

organization := "org.specs2"

scalaVersion := "2.10.4"

/** Shell */
shellPrompt := { state => "specs2> " }

/** Dependencies */
resolvers ++= 
    Seq(Resolver.sonatypeRepo("releases"), 
        Resolver.sonatypeRepo("snapshots"),
        Resolver.typesafeRepo("releases"),
        "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases")
  

libraryDependencies <++= version { version =>
  Seq(
    "org.specs2"      %% "specs2-core"         ,
    "org.specs2"      %% "specs2-matcher-extra",
    "org.specs2"      %% "specs2-gwt"          ,
    "org.specs2"      %% "specs2-html"         ,
    "org.specs2"      %% "specs2-form"         ,
    "org.specs2"      %% "specs2-scalacheck"   ,
    "org.specs2"      %% "specs2-mock"         ,
    "org.specs2"      %% "specs2-junit"        
  ).map(_ % version) ++ Seq("com.chuusai"  % s"shapeless_2.10.4" % "2.0.0")
}

scalacOptions ++= Seq("-Yrangepos", "-deprecation", "-unchecked", "-feature", "-language:_")

logBuffered := false

/** Console */
initialCommands in console in Test := "import org.specs2._"

