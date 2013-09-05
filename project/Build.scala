import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "metascrape-service"
  val appVersion      = "1.1-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "com.beachape.metascraper" %% "metascraper" % "0.0.3",
    "org.scalatest" %% "scalatest" % "1.9.1" % "test"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    scalaVersion := "2.10.2"
  )

}
