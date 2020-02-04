package com.github.cerst.auto_require

object Readme {

  final case class Person(age: Int, name: String) {
    autoRequire[Person](age >= 14 && name.nonEmpty)
  }

  def main(args: Array[String]): Unit = {
    val _ = Person(10, "John")
  }

}
