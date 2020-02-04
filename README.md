# auto_require

_auto\_require_ is a macro-powered implementation of Scala's require that comes with auto-generated error messages.  
It has no dependencies other than _scala-reflect_.

## Usage

```scala
libraryDependencies ++= Seq(
    "com.github.cerst" %% "auto_require" % autoRequireVersion
)
```

````scala
import com.github.cerst.auto_require._

final case class Person(age: Int, name: String) {
    autoRequire[Person](age >= 14 && name.nonEmpty)
}

val _ = Person(10, "John")
// Exception in thread "main" [...]: Requirement failed for 'Person':
//   Person.this.age >= 14 && scala.Predef.augmentString(Person.this.name).nonEmpty = false
//     Person.this.age >= 14 = false
//       Person.this.age = 10
//     scala.Predef.augmentString(Person.this.name).nonEmpty = true
````

Whereas you can use _autoRequire_ everywhere, you always need to specify a generic type as the latter is displayed in the error message.   
If you prefer not throwing exceptions, you can use _autoRequireEither_.  
There are a few ways to customize the error message. Please check the Scaladoc _com.github.cerst.auto_require.DisplayConfig_
and its sub-types for more info.
