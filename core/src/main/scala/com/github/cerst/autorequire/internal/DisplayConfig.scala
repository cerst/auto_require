package com.github.cerst.autorequire.internal

import com.github.cerst.autorequire.{DisplayConfigEntry, Debug, For, StripTypeNameSuffix}

import scala.quoted.*
import scala.compiletime.error

case class DisplayConfig(debug: Boolean, numNameSegments: Int, stripTypeNameSuffix: Boolean)

object DisplayConfig {

  def parse[A: Type](displayConfigEntries: Expr[Seq[DisplayConfigEntry[A]]])(using quotes: Quotes): DisplayConfig = {
    import quotes.reflect.*

    var debug = false
    var numNameSegments = 0
    var stripTypeNameSuffix = true

    displayConfigEntries match {
      case Varargs(entries) =>
        entries.foreach { entry =>
          entry match {
            case '{ Debug } =>
              debug = true

            case '{ For($maybeInt: Int) } =>
              if (Type.show[A] == Type.show[Any])
                report.throwError("'For' must have an explicit type parameter", entry)
              else
                maybeInt.value match {
                  case None =>
                    report.throwError("The value of 'For' must be a literal (plain) int", maybeInt)
                  case Some(int) if int <= 0 =>
                    report.throwError("The value of 'For' must be a positive int", maybeInt)
                  case Some(int) =>
                    numNameSegments = int
                }

            case '{ StripTypeNameSuffix($maybeBoolean: Boolean) } =>
              maybeBoolean.value match {
                case None =>
                  report.throwError(
                    "The value of 'StripTypeNameSuffix' must be a literal (plain boolean)",
                    maybeBoolean
                  )
                case Some(boolean) =>
                  stripTypeNameSuffix = boolean
              }

            case other =>
              report.throwError("Unsupported DisplayConfigEntry", other)
          }
        }

      case _ =>
        report.error(displayConfigEntries.show)
        report.throwError("Parameter 'displayConfigEntries' is not a Varargs", displayConfigEntries)
    }

    DisplayConfig(debug, numNameSegments, stripTypeNameSuffix)
  }

}
