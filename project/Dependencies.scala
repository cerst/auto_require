import sbt._

object Dependencies {

  val additionalResolvers: Seq[Resolver] = Seq()

  private object Version {
    val Newtype = "0.4.3"
    val Paradise = "2.1.1"
    val Scalatest = "3.0.8"
    val Silencer = "1.4.4"
  }

  // comment licenses for dependencies using the SPDX short identifier (see e.g. https://opensource.org/licenses/Apache-2.0)
  // rationale: double check the license when adding a new library avoid having to remove a problematic one later on when it is in use and thus hard to remove
  object Library {
    // Apache-2.0
    val Newtype = "io.estatico" %% "newtype" % Version.Newtype
    // Apache-2.0
    val ScalaReflect = "org.scala-lang" % "scala-reflect" % CommonValues.scalaVersion
    // Apache-2.0
    val Scalatest = "org.scalatest" %% "scalatest" % Version.Scalatest
    // Apache-2.0
    // always only used for compilation
    val SilencerLib = "com.github.ghik" % "silencer-lib" % Version.Silencer % Provided cross CrossVersion.full
  }

  object CompilerPlugin {
    // BSD-3-Clause
    val Paradise = compilerPlugin("org.scalamacros" % "paradise" % Version.Paradise cross CrossVersion.full)
    // Apache-2.0
    val Silencer = compilerPlugin("com.github.ghik" % "silencer-plugin" % Version.Silencer cross CrossVersion.full)
  }

  def core(scalaVersionValue: String): Seq[ModuleID] = {
    val common = Seq(
      CompilerPlugin.Silencer,
      Library.Newtype % Test,
      Library.ScalaReflect,
      Library.Scalatest % Test,
      Library.SilencerLib
    )
    val versionSpecific = CrossVersion.partialVersion(scalaVersionValue) match {
      // required for tests with newtype
      // https://github.com/estatico/scala-newtype
      case Some((2, 12)) => Seq(CompilerPlugin.Paradise)
      case _             => Seq()
    }
    common ++ versionSpecific
  }

}
