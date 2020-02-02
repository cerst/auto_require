package com.github.cerst.auto_require.internal

import scala.annotation.tailrec

object DeriveSimpleName {

  // extracted to simplify testing
  // cannot easily pass-in the WeakTypeTag as the one used is macros has a different type than the regular one
  def apply(rawFullName: String, simpleNameSegments: Int, stripTypeNameSuffix: Boolean): Either[String, String] = {
    val fullName = {
      val replaced = rawFullName.replaceAll("""\$""", ".")
      if (stripTypeNameSuffix && rawFullName.endsWith(".Type")) replaced.dropRight(5) else replaced
    }
    val index = nthLastIndexOfDot(fullName, simpleNameSegments)
    if (index == -1) {
      Left(s"Could not find '$simpleNameSegments' simple name segments in '$fullName''")
    } else {
      val simpleName = fullName.substring(index + 1)
      if (simpleName == "Nothing") {
        Left("Inferred type 'Nothing' for autoRequire. Please use an explicit type such as 'autoRequire[MyType](...)'")
      } else {
        Right(simpleName)
      }
    }
  }

  private final def nthLastIndexOfDot(string: String, nth: Int): Int = nthLastIndexOfDot(string, nth, string.length)

  @tailrec
  private final def nthLastIndexOfDot(string: String, nth: Int, fromIndex: Int): Int = {
    val index = string.lastIndexOf(".", fromIndex)
    if (nth == 1) index else nthLastIndexOfDot(string, nth - 1, index - 1)
  }

}
