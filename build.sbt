/** Project */
name := "specs2-test"

organization := "org.specs2"

scalaVersion := "2.11.5"

/** Shell */
shellPrompt := { state => "specs2> " }

/** Dependencies */
resolvers ++= 
    Seq(Resolver.sonatypeRepo("releases"), 
        Resolver.sonatypeRepo("snapshots"),
        Resolver.typesafeRepo("releases"),
        "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases")
  
val scalaz = Seq(
  "org.scalaz"        %% "scalaz-core"       % "7.1.1",
  "org.scalaz"        %% "scalaz-concurrent" % "7.1.1",
  "org.scalaz.stream" %% "scalaz-stream"     % "0.7a"
)

val caliper = Seq("com.google.caliper" % "caliper" % "0.5-rc1",
                  "com.google.guava"   % "guava"   % "14.0.1" force())


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
  ).map(_ % version) ++
    scalaz ++
    caliper ++
    Seq("com.chuusai"  %% "shapeless" % "2.0.0")
}

scalacOptions ++= Seq("-Yrangepos", "-deprecation", "-unchecked", "-feature", "-language:_")

fork in run := true

javaOptions in run ++= Seq("-cp", Attributed.data((fullClasspath in Runtime).value).mkString(":"))

logBuffered := false

/** Console */
initialCommands in console in Test := "import org.specs2._"

