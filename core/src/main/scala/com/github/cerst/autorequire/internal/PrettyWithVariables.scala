package com.github.cerst.autorequire.internal

import scala.quoted.{Expr, Quotes, Type}

object PrettyWithVariables {

  case class Result(pretty: String, variables: Seq[(String, Expr[Any])], precedence: Int)

  object Precedence {
    final val Identifier = 6
    final val LiteralConstant = 6
    final val Object = 6
    final val FieldSelectionOrUnaryMethod = 6
    final val NonInfixMethod = 6
    final val Not = 5
    final val InfixOperator = 4
    final val And = 3
    final val Or = 2
    final val InfixMethod = 1
    final val Other = 0
  }

  private val InfixOperators = Set("<", "<=", "==", ">", ">=", "&&", "||")

  def apply[T: Type](debug: Boolean, expr: Expr[Boolean])(using quotes: Quotes): Result = {
    import quotes.reflect.*

    given Boolean = debug

    def recursiveResult(term: Term)(using debug: Boolean): Result = term match {

      case Apply(Apply(method: Ident, List(receiver: Ident)), args) =>
        // an extension method whose syntax tree ends-up looking like e.g. (>(birthday))(Instant.max)
        if (debug) println("matched extension-method apply: " + term.show(using Printer.TreeStructure))
        val receiverResult = recursiveResult(receiver)
        val argsResults = args.map(p => recursiveResult(p))
        resultForMethod(receiverResult, method.name, argsResults)

      case Apply(Select(Ident(name), method), Nil) =>
        // a method with no parameters called on an ident (e.g. Instant.now() )
        // in this case, we must not descend further and treat the Indent as a variable because e.g Instant on its own is no expression
        if (debug) println("matched apply (ident & no params): " + term.show(using Printer.TreeStructure))
        val pretty = s"$name.$method()"
        val variables = Seq(pretty -> term.asExpr)
        Result(pretty, variables, Precedence.NonInfixMethod)

      case Apply(Select(selectTerm, method), args) =>
        // a method called on a receiver such as 'age < 13'
        if (debug) println("matched apply: " + term.show(using Printer.TreeStructure))
        val selectTermResult = recursiveResult(selectTerm)
        val argsResults = args.map(p => recursiveResult(p))
        resultForMethod(selectTermResult, method, argsResults)

      case Apply(applyTerm, args) =>
        // a method called without a receiver such as 'println("foo")'
        if (debug) println("matched apply: " + term.show(using Printer.TreeStructure))
        val applyTermResult = recursiveResult(applyTerm)
        val argsResults = args.map(p => recursiveResult(p))
        val pretty = applyTermResult.pretty + argsResults.map(r => r.pretty).mkString("(", ", ", ")")
        val variables = argsResults.view.map(_.variables).foldLeft(applyTermResult.variables)(_ ++ _)
        val precedence = Precedence.FieldSelectionOrUnaryMethod
        Result(pretty, variables, precedence)

      case ident @ Ident(name) =>
        // string.nonEmpty <=> scala.Predef.augmentString(string).nonEmpty
        // in this case, 'scala.Predef.augmentString' is treated as an ident which however cannot be resolved as an expression
        if (debug) println("matched ident with name: " + ident.show)
        if (ident.isExpr) {
          Result(name, Seq(name -> ident.asExpr), Precedence.Identifier)
        } else {
          Result(name, Seq.empty, Precedence.Identifier)
        }

      case l: Literal =>
        if (debug) println("matched literal: " + term.show(using Printer.TreeStructure))
        Result(l.show, Seq.empty, Precedence.LiteralConstant)

      case Select(Ident(name), string) =>
        // access to field such as "person.name" or "Instant.MAX"
        // in this case, we have to treat the entire term as a variable because e.g. Instant is no expression on its own
        if (debug) println("matched select on ident: " + term.show(using Printer.TreeStructure))
        val pretty = s"$name.$string"
        val variables = Seq(pretty -> term.asExpr)
        Result(pretty, variables, Precedence.FieldSelectionOrUnaryMethod)

      case Select(selectTerm, string) =>
        if (debug) println("matched select: " + term.show(using Printer.TreeStructure))
        val selectTermResult = recursiveResult(selectTerm)
        if (string == "unary_!") {
          // '!x' is actually represented as 'x.unary_!'
          // precedence >= is incorrect here because '!!x' is not a valid expression
          val pretty =
            if (selectTermResult.precedence > Precedence.Not) "!" + selectTermResult.pretty
            else "!(" + selectTermResult.pretty + ")"
          Result(pretty, selectTermResult.variables, Precedence.FieldSelectionOrUnaryMethod)
        } else {
          // parameterless method such as 'string.nonEmpty'
          val pretty = selectTermResult.pretty + "." + string
          Result(pretty, selectTermResult.variables, Precedence.FieldSelectionOrUnaryMethod)
        }

      case _ =>
        if (debug) println("unmatched: " + term.show(using Printer.TreeStructure))
        Result(term.show, Seq.empty, Precedence.Other)
    }

    expr.asTerm match {
      case Inlined(_, _, term) =>
        if (debug) println("root-term: " + term.show(using Printer.TreeStructure))
        recursiveResult(term)
      case _ =>
        report.throwError(
          "Can only process inlined expressions (this is likely an error in the macro implementation: argument missing 'inline' keyword)"
        )
    }
  }

  private def resultForMethod(receiverResult: Result, methodName: String, argsResults: Seq[Result]): Result = {
    val variables = argsResults.view.map(_.variables).foldLeft(receiverResult.variables)(_ ++ _)
    if (InfixOperators.contains(methodName)) {
      val precedence = Precedence.InfixOperator
      val receiverPretty =
        if (receiverResult.precedence >= precedence) receiverResult.pretty
        else "(" + receiverResult.pretty + ")"
      val argsPretty =
        if (argsResults.head.precedence >= precedence) argsResults.head.pretty
        else "(" + argsResults.head.pretty + ")"
      val pretty = receiverPretty + " " + methodName + " " + argsPretty
      Result(pretty, variables, precedence)
    } else {
      val precedence = Precedence.NonInfixMethod
      val pretty =
        receiverResult.pretty + "." + methodName + argsResults.map(r => r.pretty).mkString("(", ", ", ")")
      Result(pretty, variables, precedence)
    }
  }

}
