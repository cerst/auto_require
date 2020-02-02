package com.github.cerst.auto_require.internal

import java.time.Instant

import com.github.cerst.auto_require.{InfixOnlyOperators, autoRequireEither}
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{EitherValues, FreeSpec}

import scala.util.matching.Regex

final class MacrosSpec extends FreeSpec with TypeCheckedTripleEquals with EitherValues {

  import MacrosSpec._

  "autoRequire does not compile for Nothing" in {
    assertDoesNotCompile {
      "autoRequire(PersonValue > 0)"
    }
    // make sure that altering simple name does not affect compile error
    assertDoesNotCompile {
      "autoRequire(PersonValue > 0, SimpleNameSegments(2))"
    }
  }

  "autoRequire" in {
    // simulate usage in an apply method
    val age = 5
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual = autoRequireEither[Person](!(age < 6) && birthday.compareTo(Instant.MAX) <= 0).left.value
    val expected =
      """Requirement failed for 'Person':
        |  !(age < 6) && birthday.compareTo(java.time.Instant.MAX) <= 0 = false
        |    !(age < 6) = false
        |      age < 6 = true
        |        age = 5
        |    birthday.compareTo(java.time.Instant.MAX) <= 0 = true
        |      birthday.compareTo(java.time.Instant.MAX) = -1
        |        birthday = 2000-03-31T10:50:49Z
        |        java.time.Instant.MAX = +1000000000-12-31T23:59:59.999999999Z""".stripMargin

    assert(actual === expected)
  }

  "autoRequire with !infixOnlyOperators" in {
    val age = 5
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual =
      autoRequireEither[Person](!(age < 6) && birthday.compareTo(Instant.MAX) <= 0, InfixOnlyOperators(false)).left.value
    val expected =
      """Requirement failed for 'Person':
        |  !(age < 6) && (birthday compareTo java.time.Instant.MAX) <= 0 = false
        |    !(age < 6) = false
        |      age < 6 = true
        |        age = 5
        |    (birthday compareTo java.time.Instant.MAX) <= 0 = true
        |      birthday compareTo java.time.Instant.MAX = -1
        |        birthday = 2000-03-31T10:50:49Z
        |        java.time.Instant.MAX = +1000000000-12-31T23:59:59.999999999Z""".stripMargin

    assert(actual === expected)
  }

  "autoRequire with value-class-extension methods" in {
    import InstantOps._
    val age = 5
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual = autoRequireEither[Person](!(age < 6) && birthday <= Instant.MAX).left.value
    val expected =
      """Requirement failed for 'Person':
        |  !(age < 6) && InstantOps(birthday) <= java.time.Instant.MAX = false
        |    !(age < 6) = false
        |      age < 6 = true
        |        age = 5
        |    InstantOps(birthday) <= java.time.Instant.MAX = true
        |      InstantOps(birthday) = com.github.cerst.auto_require.internal.InstantOps@38e48309
        |        birthday = 2000-03-31T10:50:49Z
        |      java.time.Instant.MAX = +1000000000-12-31T23:59:59.999999999Z""".stripMargin

    assert(actual === expected)
  }

  "autoRequire with NESTED value-class-extension methods" in {
    import NestedInstantOps._
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual = autoRequireEither[Person](birthday > Instant.MAX).left.value
    // replace is a workaround
    // you can't have a '$' inside quotes without getting a warning concerning a potentially missing interpolator
    val expected =
      """Requirement failed for 'Person':
      |  MacrosSpec.NestedInstantOps(birthday) > java.time.Instant.MAX = false
      |    MacrosSpec.NestedInstantOps(birthday) = com.github.cerst.auto_require.internal.MacrosSpec\Dollar\NestedInstantOps@38e48309
      |      birthday = 2000-03-31T10:50:49Z
      |    java.time.Instant.MAX = +1000000000-12-31T23:59:59.999999999Z""".stripMargin.replace("""\Dollar\""", "$")

    assert(actual === expected)
  }

  "autoRequire with regex" in {
    val regex = """\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}""".r
    val input = "hello"
    val actual = autoRequireEither[Person](regex.pattern.matcher(input).matches())
    val expected =
      """Requirement failed for 'Person':
        |  regex.pattern.matcher(input).matches() = false
        |    regex.pattern.matcher(input) = java.util.regex.Matcher[pattern=\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3} region=0,5 lastmatch=]
        |      regex.pattern = \d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}
        |      input = hello""".stripMargin

    assert(actual.left.value === expected)
  }

  "autoRequire with regex and value-class-extension-method" in {
    import RegexOps._
    val regex = """\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}""".r
    val input = "hello"
    val actual = autoRequireEither[Person](input.matches(regex))
    val expected =
      """Requirement failed for 'Person':
        |  RegexOps(input).matches(regex) = false
        |    RegexOps(input) = com.github.cerst.auto_require.internal.RegexOps@5e918d2
        |      input = hello
        |    regex = \d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}""".stripMargin

    assert(actual.left.value === expected)
  }

}

private object MacrosSpec {

  final case class Person(age: Int, birthday: Instant)

  final class NestedInstantOps(val instant: Instant) extends AnyVal {
    def >(that: Instant): Boolean = instant.compareTo(that) > 0
  }

  object NestedInstantOps {
    implicit def apply(instant: Instant): NestedInstantOps = new NestedInstantOps(instant)
  }

}

final class InstantOps(val instant: Instant) extends AnyVal {
  def <=(other: Instant): Boolean = instant.compareTo(other) <= 0
}

object InstantOps {
  implicit def apply(instant: Instant): InstantOps = new InstantOps(instant)
}

final class RegexOps(val string: String) extends AnyVal {
  def matches(regex: Regex): Boolean = regex.pattern.matcher(string).matches()
}

object RegexOps {
  implicit def apply(string: String): RegexOps = new RegexOps(string)
}
