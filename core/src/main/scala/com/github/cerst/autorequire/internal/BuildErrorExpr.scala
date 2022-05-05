package com.github.cerst.autorequire.internal

import scala.quoted.*

object BuildErrorExpr {

  def apply(maybeName: Option[String], pretty: String, variables: Seq[(String, Expr[_])])(using
    quotes: Quotes
  ): Expr[String] = {

    var builderExpr: Expr[StringBuilder] = {
      val forName = maybeName match {
        case None       => ""
        case Some(name) => s" for '$name'"
      }
      val headExpr = Expr(s"Requirement failed$forName: '${pretty}' { ")
      '{ new StringBuilder($headExpr) }
    }
    // for every extracted variable, append entries such that we have declarations like 'age = 6'
    variables.zipWithIndex.foreach { case ((name, expr), index) =>
      // include a leading comma after the first entry
      val nameEqualsExpr = if (index == 0) Expr(s"$name = ") else Expr(s", $name = ")
      builderExpr = '{ $builderExpr.append($nameEqualsExpr).append($expr) }
    }
    // close the variable section ( '}' ) and convert to String
    '{ $builderExpr.append(" }").toString }
  }

}
