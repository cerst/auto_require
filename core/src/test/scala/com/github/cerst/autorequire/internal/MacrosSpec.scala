/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.github.cerst.autorequire.internal

import java.time.Instant

import com.github.cerst.autorequire.{InfixOnlyOperators, autoRequireEither}
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.EitherValues
import org.scalatest.freespec.AnyFreeSpec

import scala.util.matching.Regex

final class MacrosSpec extends AnyFreeSpec with TypeCheckedTripleEquals with EitherValues {

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
      "Requirement failed for 'Person': '!(age < 6) && birthday.compareTo(java.time.Instant.MAX) <= 0' { age = 5, birthday = 2000-03-31T10:50:49Z, java.time.Instant.MAX = +1000000000-12-31T23:59:59.999999999Z }"
    assert(actual === expected)
  }

  "autoRequire with !infixOnlyOperators" in {
    val age = 5
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual =
      autoRequireEither[Person](!(age < 6) && birthday.compareTo(Instant.MAX) <= 0, InfixOnlyOperators(false)).left.value
    val expected =
      "Requirement failed for 'Person': '!(age < 6) && (birthday compareTo java.time.Instant.MAX) <= 0' { age = 5, birthday = 2000-03-31T10:50:49Z, java.time.Instant.MAX = +1000000000-12-31T23:59:59.999999999Z }"
    assert(actual === expected)
  }

  "autoRequire with value-class-extension methods" in {
    import InstantOps._
    val age = 5
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual = autoRequireEither[Person](!(age < 6) && birthday <= Instant.MAX).left.value
    val expected =
      "Requirement failed for 'Person': '!(age < 6) && InstantOps(birthday) <= java.time.Instant.MAX' { age = 5, birthday = 2000-03-31T10:50:49Z, java.time.Instant.MAX = +1000000000-12-31T23:59:59.999999999Z }"
    assert(actual === expected)
  }

  "autoRequire with NESTED value-class-extension methods" in {
    import NestedInstantOps._
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual = autoRequireEither[Person](birthday > Instant.MAX).left.value
    val expected =
      "Requirement failed for 'Person': 'MacrosSpec.NestedInstantOps(birthday) > java.time.Instant.MAX' { birthday = 2000-03-31T10:50:49Z, java.time.Instant.MAX = +1000000000-12-31T23:59:59.999999999Z }"

    assert(actual === expected)
  }

  "autoRequire with regex" in {
    val regex = """\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}""".r
    val input = "hello"
    val actual = autoRequireEither[Person](regex.pattern.matcher(input).matches())
    val expected =
      """Requirement failed for 'Person': 'regex.pattern.matcher(input).matches()' { regex.pattern = \d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}, input = hello }"""
    assert(actual.left.value === expected)
  }

  "autoRequire with regex and value-class-extension-method" in {
    import RegexOps._
    val regex = """\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}""".r
    val input = "hello"
    val actual = autoRequireEither[Person](input.matches(regex))
    val expected =
      """Requirement failed for 'Person': 'RegexOps(input).matches(regex)' { input = hello, regex = \d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3} }"""
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
