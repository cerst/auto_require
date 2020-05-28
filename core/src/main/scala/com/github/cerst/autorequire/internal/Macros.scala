/*
 * Copyright 2020 Constantin Gerstberger
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.github.cerst.autorequire.internal

import com.github.cerst.autorequire.{DisplayConfig, InfixOnlyOperators, SimpleNameSegments, StripTypeNameSuffix}
import com.github.ghik.silencer.silent

import scala.reflect.macros.blackbox

final class Macros(val c: blackbox.Context) {
  import Macros._
  import c.universe._

  case class Result(pretty: String, declarations: List[c.Tree], debugs: List[c.Tree], precedence: Int)

  // build sub-expressions by recursively descending the tree
  // to prevent repeated evaluation of sub-expressions, go bottom (leaf) up and store intermediate sub-expressions in variables (this is the transformation)
  @silent // pattern match variables are incorrect reported as never used
  private def process(tree: c.Tree)(implicit infixOnlyOperators: Boolean): Result = tree match {
    // method invocation
    case q"$recv.$method(..$args)" =>
      val recvResult =
        if (method == TermName("apply")) {
          // skip transforming the receiver of an apply method (e.g. value class constructor) as evaluating the former simply returns the object hash
          // the expression matching the constructor call itself likely cannot be optimized because we have to traverse its arguments to output their values
          Result(showCode(recv), List.empty, List.empty, Precedence.Object)
        } else {
          process(recv)
        }
      val (argsPrettys, argsDecls, argsDebugs, argsPrecedences) = args
        .map(arg => process(arg))
        .foldLeft {
          (List.empty[String], List.empty[c.Tree], List.empty[c.Tree], List.empty[Int])
        } {
          case ((accPrettys, accDecls, accDebugs, accPrecedences), result) =>
            (
              accPrettys :+ result.pretty,
              accDecls ++ result.declarations,
              result.debugs ++ accDebugs,
              accPrecedences :+ result.precedence
            )
        }
      val (precedence, pretty) = {
        val methodNiceName = method.asInstanceOf[TermName].decodedName.toString
        if (methodNiceName == "apply") {
          // hide apply method
          val precedence = Precedence.NonInfixMethod
          val pretty = recvResult.pretty + argsPrettys.mkString("(", ",", ")")
          (precedence, pretty)
        } else if (args.length == 1 && (!infixOnlyOperators || Operators.contains(methodNiceName))) {
          // use infix syntax
          val precedence = if (Operators contains methodNiceName) Precedence.InfixOperator else Precedence.InfixMethod
          val left =
            if (recvResult.precedence >= precedence) recvResult.pretty else "(" + recvResult.pretty + ")"
          val right =
            if (argsPrecedences.head >= precedence) argsPrettys.head else "(" + argsPrettys.head + ")"
          val pretty = left + " " + methodNiceName + " " + right
          (precedence, pretty)
        } else {
          // default method display
          val precedence = Precedence.NonInfixMethod
          val pretty = recvResult.pretty + "." + methodNiceName + argsPrettys.mkString("(", ",", ")")
          (precedence, pretty)
        }
      }
      Result(pretty, recvResult.declarations ++ argsDecls, (recvResult.debugs ++ argsDebugs), precedence)

    // supported unary methods
    case q"$recv.$unary" if unary == TermName("unary_$bang") =>
      val recvResult = process(recv)
      // precedence >= is incorrect here because '!!x' is not a valid expression
      val pretty =
        if (recvResult.precedence > Precedence.Not) "!" + recvResult.pretty else "!(" + recvResult.pretty + ")"
      Result(pretty, recvResult.declarations, recvResult.debugs, Precedence.Not)

    // field selection or unsupported unary method
    // in case of the former, de-constructing the receiver further does not work and shouldn't be needed
    case q"$recv.$fieldOrUnary" =>
      val evaluated = q"$recv.$fieldOrUnary"
      val pretty = showCode(evaluated)
      val (declaration, debug) = refDeclDebug(evaluated, pretty)
      Result(pretty, List(declaration), List(debug), Precedence.FieldSelection)

    // literal constants (e.g. '10')
    case Literal(_) =>
      val pretty = showCode(tree)
      // don't create declaration + debug for literals as this debug as '10 = 10'
      Result(pretty, List.empty, List.empty, Precedence.LiteralConstant)

    // plain identifier (e.g. 'itemId')
    case _: Ident =>
      val evaluated = tree
      val pretty = showCode(evaluated)
      val (declaration, debug) = refDeclDebug(tree, pretty)
      Result(pretty, List(declaration), List(debug), Precedence.Identifier)

    // leave the rest as is and don't derive sub-expressions (mostly because we don't know what it is)
    case _ =>
      Result(showCode(tree), List.empty, List.empty, Precedence.Other)
  }

  private def refDeclDebug(tree: Tree, pretty: String): (c.Tree, c.Tree) = {
    val name: c.TermName = c.freshName(TermName("temp"))
    val reference = q"$name"
    val declaration = q"val $name = $tree"
    val debug: c.Tree = q""" $pretty + " = " + $reference """

    (declaration, debug)
  }

  private def genResult[A: c.WeakTypeTag](expression: c.Tree,
                                          onError: c.Tree,
                                          onSuccess: c.Tree,
                                          overrides: Seq[c.Expr[DisplayConfig]]): c.Tree = {

    var infixOnlyOperators = true
    var simpleNameSegments = 1
    var stripTypeNameSuffix = true

    overrides.map(exp => c.eval(c.Expr(c.untypecheck(exp.tree.duplicate)))) foreach {
      case InfixOnlyOperators(value)                => infixOnlyOperators = value
      case SimpleNameSegments(value) if (value < 1) => c.abort(c.enclosingPosition, "SimpleNameSegment must be >= 1")
      case SimpleNameSegments(value)                => simpleNameSegments = value
      case StripTypeNameSuffix(value)               => stripTypeNameSuffix = value
    }

    val rawFullName = weakTypeTag[A].tpe.typeSymbol.fullName
    val simpleName = DeriveSimpleName(rawFullName, simpleNameSegments, stripTypeNameSuffix)
      .fold(left => c.abort(c.enclosingPosition, left), identity)
    val result = process(expression)(infixOnlyOperators)

    q"""
       if (!${expression}) {
          ..${result.declarations}
          val printedValues = List(..${result.debugs})
          val msg = "Requirement failed for '" + $simpleName + "': '" + ${result.pretty} + "' " + printedValues.mkString("{ ", ", ", " }")
          $onError
        } else {
          $onSuccess
        }
     """
  }

  def autoRequire[A: c.WeakTypeTag](expression: c.Tree, overrides: c.Expr[DisplayConfig]*): c.Tree = {
    val onError =
      q"""
         import scala.util.control.NoStackTrace
         throw new IllegalArgumentException(msg) with NoStackTrace
       """
    val onSuccess = q"""()"""
    genResult(expression, onError, onSuccess, overrides)
  }

  def autoRequireEither[A: c.WeakTypeTag](expression: c.Tree, overrides: c.Expr[DisplayConfig]*): c.Tree = {
    val onError: c.Tree = q"""Left { msg }"""
    val onSuccess: c.Tree = q"""Right { () }"""
    genResult(expression, onError, onSuccess, overrides)
  }

}

object Macros {

  val Indentation = "  "
  val Operators = Set("<", "<=", ">", ">=", "&&", "||")

}
