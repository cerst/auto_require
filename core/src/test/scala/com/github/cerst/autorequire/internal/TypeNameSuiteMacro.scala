package com.github.cerst.autorequire.internal

import com.github.cerst.autorequire.StripTypeNameSuffix
import scala.quoted.*

object TypeNameSuiteMacro {

  inline def parse[A](inline numSegments: Int, inline stripTypeNameSuffix: Boolean): Option[String] =
    ${ parseImpl[A]('numSegments, 'stripTypeNameSuffix) }

  private def parseImpl[A: Type](numSegmentsExpr: Expr[Int], stripTypeNameSuffixExpr: Expr[Boolean])(using
    ctx: Quotes
  ): Expr[Option[String]] = {
    import ctx.reflect.*
    val numSegments = numSegmentsExpr.valueOrError
    val stripTypeNameSuffix = stripTypeNameSuffixExpr.valueOrError
    val res = TypeName.parse[A](numSegments, stripTypeNameSuffix)
    Expr(res)
  }
}
