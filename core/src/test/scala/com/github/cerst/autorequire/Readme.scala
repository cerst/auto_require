/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.github.cerst.autorequire

import java.time.Instant

final case class Person(age: Int, name: String, birth: Instant) {
  autoRequire(!(age < 13) && name.nonEmpty, For[Person](1))
  //    autoRequire[Person](name.nonEmpty)
  //    autoRequire[Person](!(age < 13))
  //      autoRequire[Person](age > 14 && !name.isEmpty && birth.isAfter(Instant.EPOCH))
}

object Person {
  def apply(age: Int, name: String, birth: Instant): Person = {
//    autoRequire(!(age < 13) && name.nonEmpty, InfixOnlyOperators(true), For[Person](2))
    new Person(age, name, birth)
  }

}

object Main {
  def main(args: Array[String]): Unit = {
    val _ = Person(12, "John", Instant.parse("2020-01-01T00:00:00.0Z"))
  }
}
