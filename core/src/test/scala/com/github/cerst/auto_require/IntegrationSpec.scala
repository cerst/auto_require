/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.github.cerst.auto_require

import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import org.scalatest.{Assertions, FreeSpec}

import scala.util.control.NoStackTrace

final class IntegrationSpec extends FreeSpec with Assertions {

  import IntegrationSpec._

  "works with newtype" in {
    assertThrows[IllegalArgumentException with NoStackTrace] {
      NewtypePersonId(-1)
    }
  }

  "works with AnyVal" in {
    assertThrows[IllegalArgumentException with NoStackTrace] {
      AnyValPersonId(-1)
    }
  }

}

object IntegrationSpec {

  @newtype
  final class NewtypePersonId(asInt: Int)

  object NewtypePersonId {
    def apply(int: Int): NewtypePersonId = {
      autoRequire[NewtypePersonId](int > 0)
      int.coerce
    }
  }

  final case class AnyValPersonId(asInt: Int)

  object AnyValPersonId {
    def apply(int: Int): AnyValPersonId = {
      autoRequire[NewtypePersonId](int > 0)
      new AnyValPersonId(int)
    }
  }

}
