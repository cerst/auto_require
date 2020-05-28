/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.github.cerst.autorequire.internal

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.{Assertions, EitherValues}

import scala.reflect.runtime.universe.{WeakTypeTag, weakTypeTag}

final class DeriveSimpleNameSpec extends AnyFreeSpec with Assertions with TypeCheckedTripleEquals with EitherValues {

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
    assert(actual_2 contains "NameSubject")
  }

  "2 segments" in {
    val actual_1 = DeriveSimpleName(rawFullNameOf[NameSubject], 2, stripTypeNameSuffix = false)
    val actual_2 = DeriveSimpleName(rawFullNameOf[NameSubject], 2, stripTypeNameSuffix = true)
    assert(actual_1 == actual_2)
    assert(actual_2 contains "DeriveSimpleNameSpec.NameSubject")
  }

  "strip 'Type' suffix" in {
    assert(rawFullNameOf[Type].endsWith(".Type"))
    assert(DeriveSimpleName(rawFullNameOf[Type], 1, stripTypeNameSuffix = false) contains "Type")
    assert(DeriveSimpleName(rawFullNameOf[Type], 1, stripTypeNameSuffix = true) contains "DeriveSimpleNameSpec")
    assert(
      DeriveSimpleName(rawFullNameOf[Type], 2, stripTypeNameSuffix = true) contains "internal.DeriveSimpleNameSpec"
    )
  }

}

private object DeriveSimpleNameSpec {

  type Type

  final case class NameSubject()

  def rawFullNameOf[A: WeakTypeTag]: String = weakTypeTag[A].tpe.typeSymbol.fullName

}
