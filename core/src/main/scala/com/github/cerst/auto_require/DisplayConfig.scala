package com.github.cerst.auto_require

/**
  * Sub-types of this trait change the error message generation of <i>auto_require</i> functions.
  */
sealed trait DisplayConfig

/**
  * Whether to infix single arg methods beside the standard operators such as '>', '==', ...
  * <p>
  * Example:
  * {{{
  *   Instant.now.compareTo(otherInstant)   // false
  *   (Instant.now compareTo otherInstant)  // true
  * }}}
  * <p>
  * Default: false
  */
final case class InfixOnlyOperators(value: Boolean) extends DisplayConfig

/**
  * How many segments to use of a full name as simple name (mostly useful for nested types).<br/>
  * Requesting more segment than available will result in a compile error.
  * <p>
  * Example:
  * {{{
  *   val fullName = com.example.Object.NestedType
  *   val simpleName_1 = NestedType         // 1
  *   val simpleName_2 = Object.NestedType  // 2
  * }}}
  * <p>
  * Default: 1
  */
final case class SimpleNameSegments(value: Int) extends DisplayConfig

/**
  * Whether to strip the suffix '.Type' from a full name before taking segments as simple name.<br/>
  * This is mostly relevant for tagged value class implementations such as scala-newtype
  * <p>
  * Example:
  * {{{
  *   package object types {
  *     @newtype
  *     final case class WidgetId(toInt: Int)
  *
  *     val fullNameFalse = package.WidgetId.Type   // false
  *     val fullNameTrue  = package.WidgetId        // true
  *   }
  * }}}
  * <p>
  *  Default: true
  *
  * @see [[https://github.com/estatico/scala-newtype]]
  */
final case class StripTypeNameSuffix(value: Boolean) extends DisplayConfig
