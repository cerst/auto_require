import sbt._

object Dependencies {

  val resolvers: Seq[Resolver] = Seq()

  object Version {
    val Scalatest = "3.0.8"
    val Silencer = "1.4.4"
  }

  // comment licenses for dependencies using the SPDX short identifier (see e.g. https://opensource.org/licenses/Apache-2.0)
  // rationale: double check the license when adding a new library avoid having to remove a problematic one later on when it is in use and thus hard to remove
  object Library {
    // Apache-2.0
    val ScalaReflect = "org.scala-lang" % "scala-reflect" % CommonValues.scalaVersion
    // Apache-2.0
    val Scalatest = "org.scalatest" %% "scalatest" % Version.Scalatest
    // Apache-2.0
    val SilencerCompilerPlugin = compilerPlugin(
      "com.github.ghik" % "silencer-plugin" % Version.Silencer cross CrossVersion.full
    )
    // Apache-2.0
    // always only used for compilation
    val SilencerLib = "com.github.ghik" % "silencer-lib" % Version.Silencer % Provided cross CrossVersion.full
  }

  val coreLibraries: Seq[ModuleID] =
    Seq(Library.ScalaReflect, Library.Scalatest % Test, Library.SilencerCompilerPlugin, Library.SilencerLib)
}
