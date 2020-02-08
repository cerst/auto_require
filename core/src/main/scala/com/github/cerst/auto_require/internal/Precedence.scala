/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.github.cerst.auto_require.internal

object Precedence {
  final val Identifier = 6
  final val LiteralConstant = 6
  final val Object = 6
  final val FieldSelection = 6
  final val NonInfixMethod = 6
  final val Not = 5
  /** e.g.: >, >=, <, <= */
  final val InfixOperator = 4
  final val And = 3
  final val Or = 2
  final val InfixMethod = 1
  final val Other = 0
}
