/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.github.cerst.autorequire

inline def autoRequireEither[A](
  inline expression: Boolean,
  inline displayConfigEntries: DisplayConfigEntry[A]*
): Either[String, Unit] = {
  ${ internal.autoRequireEitherImpl[A]('expression, 'displayConfigEntries) }
}

inline def autoRequire[A](inline expression: Boolean, inline displayConfigEntries: DisplayConfigEntry[A]*): Unit = {
  ${ internal.autoRequireImpl[A]('expression, 'displayConfigEntries) }
}
