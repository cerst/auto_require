package com.github.cerst.autorequire.internal

import com.github.cerst.autorequire.{DisplayConfigEntry, For, StripTypeNameSuffix}
import org.scalacheck.Gen
import munit.FunSuite
import org.scalacheck.Prop._

import scala.quoted

class DisplayConfigSuite extends FunSuite {

  test("defaults") {
    val actual = DisplayConfigSuiteMacro.parse()
    val expected = (false, 0, true)
    assert(clue(actual) == clue(expected))
  }

  test("StripTypeNameSuffix: true") {
    val actual = DisplayConfigSuiteMacro.parse(StripTypeNameSuffix(true))
    val expected = (false, 0, true)
    assert(clue(actual) == clue(expected))
  }

  test("StripTypeNameSuffix: false") {
    val actual = DisplayConfigSuiteMacro.parse(StripTypeNameSuffix(false))
    val expected = (false, 0, false)
    assert(clue(actual) == clue(expected))
  }

  test("For - any") {
    val actual = compileErrors { "DisplayConfigSuiteMacro.parse(For(3))" }
    val expected =
      """error: 'For' must have an explicit type parameter
        |DisplayConfigSuiteMacro.parse(For(3))
        |                                ^""".stripMargin
    assertEquals(actual, expected)
  }

  test("For - negative value") {
    val actual = compileErrors { "DisplayConfigSuiteMacro.parse(For[Int](-1))" }
    val expected =
      """error: The value of 'For' must be a positive int
        |DisplayConfigSuiteMacro.parse(For[Int](-1))
        |                                      ^""".stripMargin
    assertEquals(actual, expected)
  }

  test("For - zero value") {
    val actual = compileErrors { "DisplayConfigSuiteMacro.parse(For[Int](0))" }
    val expected =
      """error: The value of 'For' must be a positive int
        |DisplayConfigSuiteMacro.parse(For[Int](0))
        |                                      ^""".stripMargin
    assertEquals(actual, expected)
  }

  test("For - valid") {
    // we have to use a literal (plain number) - otherwise, the macro cannot access the value
    val actual = DisplayConfigSuiteMacro.parse(For[Int](3))
    val expected = (false, 3, true)
    assert(clue(actual) == clue(expected))
  }

}
