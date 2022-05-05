/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.github.cerst.autorequire.internal

import munit.FunSuite

final class TypeNameSuite extends FunSuite {

  import TypeNameSuite._

  test("1 segment") {
    val actual_1 = TypeNameSuiteMacro.parse[NameSubject](1, stripTypeNameSuffix = false)
    val actual_2 = TypeNameSuiteMacro.parse[NameSubject](1, stripTypeNameSuffix = true)

    assert(clue(actual_1) contains "NameSubject")
    assert(clue(actual_1) == clue(actual_2))
  }

  test("2 segments") {
    val actual_1 = TypeNameSuiteMacro.parse[NameSubject](2, stripTypeNameSuffix = false)
    val actual_2 = TypeNameSuiteMacro.parse[NameSubject](2, stripTypeNameSuffix = true)
    assert(clue(actual_1) contains "TypeNameSuite.NameSubject")
    assert(clue(actual_1) == clue(actual_2))
  }

  test("too many segments") {
    val actual = compileErrors("TypeNameSuiteMacro.parse(3, stripTypeNameSuffix = false)")
    val expected =
      """error: Could not find '3' name segments in 'scala.Any'
        |    val actual = compileErrors("TypeNameSuiteMacro.parse(3, stripTypeNameSuffix = false)")
        |                             ^""".stripMargin
    assertEquals(actual, expected)
  }

  test("'Type' suffix - no strip") {
    val actual = TypeNameSuiteMacro.parse[Type](2, stripTypeNameSuffix = false)
    assert(clue(actual).contains("TypeNameSuite.Type"))
  }

  test("'Type' suffix - strip") {
    val actual = TypeNameSuiteMacro.parse[Type](1, stripTypeNameSuffix = true)
    assert(clue(actual).contains("TypeNameSuite"))
  }

}

private object TypeNameSuite {

  type Type

  final case class NameSubject()

}
