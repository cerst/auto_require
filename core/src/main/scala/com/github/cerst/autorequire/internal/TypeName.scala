/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.github.cerst.autorequire.internal

import scala.annotation.tailrec
import scala.quoted.*

object TypeName {

  def parse[A: Type](forNameSegments: Int, stripTypeNameSuffix: Boolean)(using ctx: Quotes): Option[String] = {
    import ctx.reflect.*

    val typeRepr = TypeRepr.of[A]
    val rawFullName = typeRepr.typeSymbol.fullName

    if (forNameSegments == 0) {
      None
    } else {
      val fullName = {
        if (stripTypeNameSuffix && rawFullName.endsWith(".Type")) rawFullName.dropRight(5) else rawFullName
      }
      val index = nthLastIndexOfDot(fullName, forNameSegments)

      if (index == -1) {
        report.throwError(s"Could not find '$forNameSegments' name segments in '$fullName'")
      } else {
        Some { fullName.substring(index + 1).replaceAll("\\$", "") }
      }
    }
  }

  private def nthLastIndexOfDot(string: String, nth: Int): Int = nthLastIndexOfDot(string, nth, string.length)

  @tailrec
  private def nthLastIndexOfDot(string: String, nth: Int, fromIndex: Int): Int = {
    val index = string.lastIndexOf(".", fromIndex)
    if (nth == 1) index else nthLastIndexOfDot(string, nth - 1, index - 1)
  }

}
