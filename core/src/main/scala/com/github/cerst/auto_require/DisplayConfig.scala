package com.github.cerst.auto_require

sealed trait DisplayConfig

final case class InfixOnlyOperators(value: Boolean) extends DisplayConfig

final case class SimpleNameSegments(value: Int) extends DisplayConfig

final case class StripTypeNameSuffix(value: Boolean) extends DisplayConfig
