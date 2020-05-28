/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.github.cerst.autorequire

object Readme {

  final case class Person(age: Int, name: String) {
    autoRequire[Person](age >= 14 && name.nonEmpty)
  }

  def main(args: Array[String]): Unit = {
    val _ = Person(10, "John")
  }

}
