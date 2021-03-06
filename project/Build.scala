import java.nio.file.{StandardCopyOption, Files}


import sbt._
import Keys._



object MacroBuild extends Build {
  val toLib = taskKey[Unit]("Save compiled bytecode jars to ./lib")

    val buildSettings = Defaults.defaultSettings ++ Seq(
      version := "0.2-NIGHTLY",
      scalaVersion := "2.11.7",
      resolvers += Resolver.sonatypeRepo("snapshots"),
      resolvers += Resolver.sonatypeRepo("releases"),
      scalacOptions ++= Seq()
    )
  lazy val main = Project("main", file("."), settings = buildSettings ++ Seq(
    toLib := {
      val jars = (packageBin in Compile).value
      Files.copy(jars.toPath,
        baseDirectory.value / "lib" / jars.name toPath,
        StandardCopyOption.REPLACE_EXISTING)
    },
    name := "th4j.core"
  )) aggregate (generateMacro) dependsOn(generateMacro)
  lazy val generateMacro = Project("generate", file("generate"), settings = buildSettings ++ Seq(
    libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _),
    libraryDependencies := {
      libraryDependencies.value ++ Seq(
              compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full))
    },
    name:= "th4j.generate"
  ))


}

