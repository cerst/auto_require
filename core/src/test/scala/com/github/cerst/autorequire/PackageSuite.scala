/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.github.cerst.autorequire

import com.github.cerst.autorequire.autoRequireEither
import munit.FunSuite

import java.time.Instant
import java.util.UUID
import scala.util.matching.Regex

final class PackageSuite extends FunSuite {

  import PackageSuite._

  test("autoRequire - For") {
    // simulate usage in an apply method
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual = autoRequireEither(birthday.compareTo(Instant.now) > 0, For[Person](1))
    val actualError = actual.swap.getOrElse { fail("Result is not an error", clues(actual)) }
    val expectedErrorPattern =
      """Requirement failed for 'Person': 'birthday.compareTo\(Instant.now\(\)\) > 0' \{ birthday = 2000-03-31T10:50:49Z, Instant.now\(\) = .* }""".r
    assert(clue(expectedErrorPattern) matches clue(actualError))
  }

  test("autoRequire - ident & no params") {
    // simulate usage in an apply method
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual = autoRequireEither(birthday.compareTo(Instant.now) > 0)
    val actualError = actual.swap.getOrElse { fail("Result is not an error", clues(actual)) }
    val expectedErrorPattern =
      """Requirement failed: 'birthday.compareTo\(Instant.now\(\)\) > 0' \{ birthday = 2000-03-31T10:50:49Z, Instant.now\(\) = .* }""".r
    assert(clue(expectedErrorPattern) matches clue(actualError))
  }

  test("autoRequire - select on ident") {
    // simulate usage in an apply method
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual = autoRequireEither(birthday.compareTo(Instant.MIN) <= 0)
    val actualError = actual.swap.getOrElse { fail("Result is not an error", clues(actual)) }
    val expectedError =
      "Requirement failed: 'birthday.compareTo(Instant.MIN) <= 0' { birthday = 2000-03-31T10:50:49Z, Instant.MIN = -1000000000-01-01T00:00:00Z }"
    assertEquals(actualError, expectedError)
  }

  test("autoRequire - select on ident 2") {
    // simulate usage in an apply method
    val actual = autoRequireEither(Instant.MAX.toString == "")
    val actualError = actual.swap.getOrElse { fail("Result is not an error", clues(actual)) }
    val expectedError =
      "Requirement failed: 'Instant.MAX.toString() == \"\"' { Instant.MAX = +1000000000-12-31T23:59:59.999999999Z }"
    assertEquals(actualError, expectedError)
  }

  test("autoRequire - complex expression") {
    val age = 5
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual = autoRequireEither(!(age < 6) && birthday.compareTo(Instant.MAX) <= 0)
    val actualError = actual.swap.getOrElse { fail("Result is not an error", clues(actual)) }
    val expectedError =
      "Requirement failed: '!(age < 6) && birthday.compareTo(Instant.MAX) <= 0' { age = 5, birthday = 2000-03-31T10:50:49Z, Instant.MAX = +1000000000-12-31T23:59:59.999999999Z }"
    assertEquals(actualError, expectedError)
  }

  test("autoRequire with value-class-extension methods") {
    val age = 5
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual = autoRequireEither(!(age < 6) && birthday <= Instant.MAX)
    val actualError = actual.swap.getOrElse { fail("Result is not an error", clues(actual)) }
    val expectedError =
      "Requirement failed: '!(age < 6) && <=(birthday)(Instant.MAX)' { age = 5, birthday = 2000-03-31T10:50:49Z, Instant.MAX = +1000000000-12-31T23:59:59.999999999Z }"

    assertEquals(actualError, expectedError)
  }

  test("autoRequire with NESTED value-class-extension methods") {
    val birthday = Instant.parse("2000-03-31T10:50:49Z")
    val actual = autoRequireEither(birthday > Instant.MAX, Debug)
    val actualError = actual.swap.getOrElse { fail("Result is not an error", clues(actual)) }
    val expectedError =
      "Requirement failed: '>(birthday)(Instant.MAX)' { birthday = 2000-03-31T10:50:49Z, java.time.Instant.MAX = +1000000000-12-31T23:59:59.999999999Z }"

    assert(clue(actualError) == clue(expectedError))
  }

  test("autoRequire with regex") {
    val regex = """\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}""".r
    val input = "hello"
    val actual = autoRequireEither(regex.matches(input))
    val expectedError =
      """Requirement failed for 'Person': 'regex.matches(input)' { regex.pattern = \d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}, input = hello }"""
    val actualError = actual.swap.getOrElse { fail("Result is not an error", clues(actual)) }
    assert(clue(actualError) == clue(expectedError))
  }
//
//  test("autoRequire with regex and value-class-extension-method") {
//    val regex = """\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}""".r
//    val input = "hello"
//    val actual = autoRequireEither[Person](input.matches(regex))
//    val expectedError =
//      """Requirement failed for 'Person': 'RegexOps(input).matches(regex)' { input = hello, regex = \d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3} }"""
//    val actualError = actual.swap.getOrElse { fail("Result is not an error", clues(actual)) }
//    assert(actualError == expectedError)
//  }
//
  test("autoRequire with scala.Predef") {
    val name = ""
    val actual = autoRequireEither(name.nonEmpty, For[Person](1))
    val actualError = actual.swap.getOrElse { fail("Result is not an error", clues(actual)) }
    val expectedError = """Requirement failed for 'Person': 'augmentString(name).nonEmpty' { name =  }"""
    assert(clue(actualError) == clue(expectedError))
  }
//
//  test("autoRequire with negative, numeric values") {
//    val age = -2
//    val actual = autoRequireEither[Person](age > -1)
//    val actualError = actual.swap.getOrElse { fail("Result is not an error", clues(actual)) }
//    val expectedError = """Requirement failed for 'Person': 'age > -1' { age = -2 }"""
//    assert(actualError == expectedError)
//  }

}

private object PackageSuite {

  final case class Person(age: Int, birthday: Instant, name: String)

  extension (instant: Instant) {
    def >(that: Instant): Boolean = instant.compareTo(that) > 0
  }

}

extension (instant: Instant) {
  def <=(other: Instant): Boolean = instant.compareTo(other) <= 0
}

extension (string: String) {
  def matches(regex: Regex): Boolean = regex.pattern.matcher(string).matches()
}
