/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: MIT
 */

package com.github.cerst

import com.github.cerst.auto_require.internal.Macros

package object auto_require {

  /**
    * Tests the given `expression`, throwing an `IllegalArgumentException` if false.<br/>
    * In contrast to Scala's standard `require`, error messages are generated automatically.
    *
    * @param expression The expression to test.
    * @param overrides  Allows for customizing the auto-generated error message. Check `DisplayConfig` and its sub-types
    *                   for more info.
    * @tparam A Used to generate a context name as part of the error message. Must not be `Nothing`.
    * @return `Unit` if and only if the given `expression` is true. Throws an `IllegalArgumentException` otherwise.
    */
  @throws[IllegalArgumentException]
  def autoRequire[A](expression: Boolean, overrides: DisplayConfig*): Unit =
    macro Macros.autoRequire[A]

  /**
   * Tests the given `expression`, returning a `Left` containing an error message if false.<br/>
   * In contrast to Scala's standard `require`, error messages are generated automatically.
   *
   * @param expression The expression to test.
   * @param overrides  Allows for customizing the auto-generated error message. Check `DisplayConfig` and its sub-types
   *                   for more info.
   * @tparam A Used to generate a context name as part of the error message. Must not be `Nothing`.
   * @return `Left` if and only if the given `expression` is true. `Right`
   */
  def autoRequireEither[A](expression: Boolean, overrides: DisplayConfig*): Either[String, Unit] =
    macro Macros.autoRequireEither[A]

}
