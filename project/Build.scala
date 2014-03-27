import sbt._
import Keys._
import spray.revolver.RevolverPlugin.Revolver
import sbtassembly.Plugin._
import AssemblyKeys._

object ApplicationBuild extends Build {
  val appName         = "spray-example"
  val aVersion        = "0.0.6"
  val sVersion        = "2.10.3"
  val sMVersion       = "2.10"
  val env             = if (aVersion.split(".").size == 3) "dev" else "prod"
  val org             = "io.bankroll"
  val artifact        =  appName + "-" + env + "-" + aVersion


  val root = Project(appName, file("."))
    .settings(commonSettings:_*)
    .settings(assemblySettings:_ *)
    .settings(
      version := aVersion,
      parallelExecution in Test := false,
      testOptions in Test += Tests.Argument("junitxml"),
      scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),    
      libraryDependencies ++= {
        val akkaV = "2.1.4"
        val sprayV = "1.1.1"
        Seq(
          "io.spray"            %   "spray-can"      % sprayV,
          "io.spray"            %   "spray-routing"  % sprayV,
          "com.typesafe.akka"    %%  "akka-actor"    % akkaV,
          "com.github.lookfirst" %  "sardine"        % "5.1",
          "io.spray"            %   "spray-testkit" % sprayV  % "test",
          "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
          "org.specs2"          %%  "specs2"        % "2.2.3" % "test",
          "junit"                % "junit"          % "4.8.1" % "test"
        )                  
      })
  .settings(Revolver.settings :_*)
  .settings(swapVersionsTask)
  .settings(jarName in assembly := artifact + ".jar" )
  .settings(zipperTask)


  lazy val commonSettings = Seq(
    organization := org,
    scalaVersion := sVersion,
    resolvers ++= Seq(
      "spray repo" at "http://repo.spray.io/"
    )
  )

  lazy val swapVersions = TaskKey[Seq[File]]("app-version") in Compile

  lazy val zipper    = TaskKey[Unit]("zip_artifact") in Compile

  lazy val swapVersionsTask = swapVersions <<= (baseDirectory, streams, version) map {
    (baseDir, st, v) =>
      val dev_current = baseDir / "current_dev_version"
      val dev_previous = baseDir / "previous_dev_version"
      val prod_current = baseDir / "current_prod_version"
      val prod_previous = baseDir / "previous_prod_version"
      val prod_version = v.split("\\.").take(2).mkString(".")
      IO.copyFile(prod_current, prod_previous)
      IO.write(prod_current, prod_version)
      IO.copyFile(dev_current, dev_previous)
      IO.write(dev_current, v)


      st.log.info("Storing current application dev version( " + v + " ) in " + dev_current.getPath)
      st.log.info("Storing current application prod version( " + prod_version + " ) in " + prod_current.getPath)
      Seq(dev_current, dev_previous, prod_current, prod_previous)
  }

  lazy val zipperTask = zipper <<= (streams, version, target, baseDirectory) map {
    (s, v, t,b) =>
      val artFileName = "scala-" + sMVersion + artifact + ".jar"
      val sArt = t / artFileName
      val zipFileName =  artifact + ".zip"
      val zipFile = b / zipFileName
      IO.zip(Path.allSubpaths(sArt), zipFile)
  }
}
