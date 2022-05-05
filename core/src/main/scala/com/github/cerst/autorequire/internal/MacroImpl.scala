package com.github.cerst.autorequire.internal

import com.github.cerst.autorequire.DisplayConfigEntry

import scala.quoted.*

def autoRequireImpl[A: Type](expr: Expr[Boolean], displayConfigEntries: Expr[Seq[DisplayConfigEntry[A]]])(using
  Quotes
): Expr[Unit] = {
  def createOnErrorExpr(errorExpr: Expr[String]): Expr[Nothing] = '{ throw new RuntimeException($errorExpr) }
  val onSuccessExpr: Expr[Unit] = '{ () }
  impl(expr, displayConfigEntries, createOnErrorExpr, onSuccessExpr)
}

def autoRequireEitherImpl[A: Type](expr: Expr[Boolean], displayConfigEntries: Expr[Seq[DisplayConfigEntry[A]]])(using
  Quotes
): Expr[Either[String, Unit]] = {
  import quotes.reflect.*
  def createOnErrorExpr(errorExpr: Expr[String]): Expr[Left[String, Unit]] = '{ Left($errorExpr) }
  val onSuccessExpr = '{ Right { () } }
  impl(expr, displayConfigEntries, createOnErrorExpr, onSuccessExpr)
}

private def impl[A: Type, E <: R: Type, S <: R: Type, R](
  expr: Expr[Boolean],
  displayConfigEntries: Expr[Seq[DisplayConfigEntry[A]]],
  createOnErrorExpr: Expr[String] => Expr[E],
  onSuccess: Expr[S]
)(using quotes: Quotes): Expr[R] = {
  import quotes.reflect.*

  val displayConfig = DisplayConfig.parse(displayConfigEntries)
  val maybeName = TypeName.parse[A](displayConfig.numNameSegments, displayConfig.stripTypeNameSuffix)
  val result = PrettyWithVariables[A](displayConfig.debug, expr)
  val errorExpr = BuildErrorExpr(maybeName, result.pretty, result.variables)
  val onErrorExpr = createOnErrorExpr(errorExpr)

  val res = '{
    if ($expr) {
      $onSuccess
    } else {
      $onErrorExpr
    }
  }
  if (displayConfig.debug) println(res.show)
  res
}
