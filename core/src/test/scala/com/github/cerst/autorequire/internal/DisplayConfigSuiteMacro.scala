package com.github.cerst.autorequire.internal

import com.github.cerst.autorequire.DisplayConfigEntry
import scala.quoted.*

object DisplayConfigSuiteMacro {

  inline def parse[A](inline displayConfigEntries: DisplayConfigEntry[A]*): (Boolean, Int, Boolean) =
    ${ parseImpl('displayConfigEntries) }

  private def parseImpl[A: Type](
    displayConfigEntries: Expr[Seq[DisplayConfigEntry[A]]]
  )(using ctx: Quotes): Expr[(Boolean, Int, Boolean)] = {
    import ctx.reflect.*
    // causes a compile error
    val actual = DisplayConfig.parse(displayConfigEntries)
    Expr { (actual.debug, actual.numNameSegments, actual.stripTypeNameSuffix) }
  }

}
