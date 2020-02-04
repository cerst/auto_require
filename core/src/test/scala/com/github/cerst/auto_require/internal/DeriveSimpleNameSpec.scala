/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: MIT
 */

package com.github.cerst.auto_require.internal

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{Assertions, EitherValues, FreeSpec}

import scala.reflect.runtime.universe.{WeakTypeTag, weakTypeTag}

final class DeriveSimpleNameSpec extends FreeSpec with Assertions with TypeCheckedTripleEquals with EitherValues {

  import DeriveSimpleNameSpec._

  "class tag full name contains '$'" in {
    assert(classOf[NameSubject].getName contains '$')
  }

  "weak type tag full name does not contain '$'" in {
    assert(!weakTypeTag[NameSubject].tpe.typeSymbol.fullName.contains('$'))
  }

  "1 segment" in {
    val actual_1 = DeriveSimpleName(rawFullNameOf[NameSubject], 1, stripTypeNameSuffix = false)
    val actual_2 = DeriveSimpleName(rawFullNameOf[NameSubject], 1, stripTypeNameSuffix = true)
    assert(actual_1 == actual_2)
    assert(actual_2.right.value === "NameSubject")
  }

  "2 segments" in {
    val actual_1 = DeriveSimpleName(rawFullNameOf[NameSubject], 2, stripTypeNameSuffix = false)
    val actual_2 = DeriveSimpleName(rawFullNameOf[NameSubject], 2, stripTypeNameSuffix = true)
    assert(actual_1 == actual_2)
    assert(actual_2.right.value === "DeriveSimpleNameSpec.NameSubject")
  }

  "strip 'Type' suffix" in {
    assert(rawFullNameOf[Type].endsWith(".Type"))
    assert(DeriveSimpleName(rawFullNameOf[Type], 1, stripTypeNameSuffix = false).right.value === "Type")
    assert(DeriveSimpleName(rawFullNameOf[Type], 1, stripTypeNameSuffix = true).right.value === "DeriveSimpleNameSpec")
    assert(
      DeriveSimpleName(rawFullNameOf[Type], 2, stripTypeNameSuffix = true).right.value === "internal.DeriveSimpleNameSpec"
    )
  }

}

private object DeriveSimpleNameSpec {

  type Type

  final case class NameSubject()

  def rawFullNameOf[A: WeakTypeTag]: String = weakTypeTag[A].tpe.typeSymbol.fullName

}
