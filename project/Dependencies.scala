import sbt._

object Dependencies {

  val additionalResolvers: Seq[Resolver] = Seq()

  private object Version {
    val MUnit = "0.7.26"
  }

  // comment licenses for dependencies using the SPDX short identifier (see e.g. https://opensource.org/licenses/Apache-2.0)
  // rationale: double check the license when adding a new library avoid having to remove a problematic one later on when it is in use and thus hard to remove
  private object Library {
    // Apache-2.0
    val MUnitScalaCheck = "org.scalameta" %% "munit-scalacheck" % Version.MUnit
  }

  val core: Seq[ModuleID] = {
    val compile = Seq()
    val test = Seq(Library.MUnitScalaCheck).map(_ % Test)
    compile ++ test
  }

}
