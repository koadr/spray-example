import sbt._
import Keys._
import eu.diversit.sbt.plugin.WebDavPlugin._
import spray.revolver.RevolverPlugin.Revolver

object ApplicationBuild extends Build {
  val appName         = "spray-example"
  val appVersion      = "0.1-SNAPSHOT"


  val root = Project(appName, file("."))
    .settings(commonSettings:_*)
    .settings(
      version := appVersion,
      parallelExecution in Test := false,
      testOptions in Test += Tests.Argument("junitxml"),
      scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),    
      libraryDependencies ++= {
        val akkaV = "2.1.4"
        val sprayV = "1.1.1"
        Seq(
          "io.spray"            %   "spray-can"     % sprayV,
          "io.spray"            %   "spray-routing" % sprayV,
          "io.spray"            %   "spray-testkit" % sprayV  % "test",
          "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
          "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
          "org.specs2"          %%  "specs2"        % "2.2.3" % "test",
          "junit"                % "junit"          % "4.8.1" % "test"
        )                  
      },     
      publishTo <<= version { v: String =>
        val cloudbees = "https://repository-koadr.forge.cloudbees.com/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("Cloudbees snapshots" at cloudbees + "snapshot")
        else
          Some("Cloudbees releases" at cloudbees + "release")
      },     
      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
    )
  .settings(Revolver.settings :_*)
  .settings(WebDav.globalSettings : _*)

  // Set the version as the system property
  System.setProperty("APP_VERSION", appVersion)
  

  lazy val commonSettings = Seq(
    organization := "io.bankroll",
    scalaVersion := "2.10.3",
    resolvers ++= Seq(
      "spray repo" at "http://repo.spray.io/"
    )
  )

}
