import sbt._

/**
  * Stores variable used in multiple places of the build
  */
object CommonValues {

  val connection = "git@github.com:cerst/auto_require.git"
  val homepage = url("https://github.com/cerst/auto_require")
  val organizationName = "Constantin Gerstberger"
  val scalaVersion = "2.13.3"
  val startYear = 2020

  val crossScalaVersions = List(scalaVersion, "2.12.12")

}
