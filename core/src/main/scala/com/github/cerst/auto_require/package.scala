package com.github.cerst

import com.github.cerst.auto_require.internal.Macros

package object auto_require {

  def autoRequire[A](expression: Boolean, overrides: DisplayConfig*): Unit =
    macro Macros.autoRequire[A]

  def autoRequireEither[A](expression: Boolean, overrides: DisplayConfig*): Either[String, Unit] =
    macro Macros.autoRequireEither[A]

}
