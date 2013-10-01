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
    "com.beachape.metascraper" %% "metascraper" % "0.2.3",
    "com.github.mumoshu" %% "play2-memcached" % "0.3.0.2",
    "org.scalatest" %% "scalatest" % "1.9.1" % "test"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    resolvers += "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases",
    resolvers += "Spy Repository" at "http://files.couchbase.com/maven2",
    scalaVersion := "2.10.2"
  )

}
