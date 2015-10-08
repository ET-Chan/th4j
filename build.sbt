name := "JTH"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("releases")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)

libraryDependencies ++= Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value,
	
  "com.typesafe.play" %% "play-json" % "2.3.4",
  "net.java.dev.jna" % "jna" % "4.2.0",
  compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full))

scalacOptions ++= Seq("-Ymacro-debug-lite")


//publishTo := Some(Resolver.file("file", new File("./lib")))
//publishArtifact in (Compile, packageSrc) := false

