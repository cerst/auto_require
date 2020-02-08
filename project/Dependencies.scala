import sbt._

object Dependencies {

  val additionalResolvers: Seq[Resolver] = Seq()

  private object Version {
    val Newtype = "0.4.3"
    val Paradise = "2.1.1"
    val Scalatest = "3.1.0"
    val Silencer = "1.4.4"
  }

  // comment licenses for dependencies using the SPDX short identifier (see e.g. https://opensource.org/licenses/Apache-2.0)
  // rationale: double check the license when adding a new library avoid having to remove a problematic one later on when it is in use and thus hard to remove
  object Library {
    // Apache-2.0
    val Newtype = "io.estatico" %% "newtype" % Version.Newtype
    // Apache-2.0
    val Scalatest = "org.scalatest" %% "scalatest" % Version.Scalatest
    // Apache-2.0
    // always only used for compilation
    val SilencerLib = "com.github.ghik" % "silencer-lib" % Version.Silencer % Provided cross CrossVersion.full
    // Apache-2.0
    def scalaReflect(scalaVersionValue: String) = "org.scala-lang" % "scala-reflect" % scalaVersionValue
  }

  object CompilerPlugin {
    // BSD-3-Clause
    val Paradise = compilerPlugin("org.scalamacros" % "paradise" % Version.Paradise cross CrossVersion.full)
    // Apache-2.0
    val Silencer = compilerPlugin("com.github.ghik" % "silencer-plugin" % Version.Silencer cross CrossVersion.full)
  }

  def core(scalaVersionValue: String): Seq[ModuleID] = {
    val cross = Seq(
      CompilerPlugin.Silencer,
      Library.Newtype % Test,
      Library.Scalatest % Test,
      Library.SilencerLib,
      Library.scalaReflect(scalaVersionValue)
    )
    val specific = CrossVersion.partialVersion(scalaVersionValue) match {
      // required for tests with newtype
      // https://github.com/estatico/scala-newtype
      case Some((2, 12)) => Seq(CompilerPlugin.Paradise)
      case _             => Seq()
    }
    cross ++ specific
  }

}
