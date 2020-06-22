# auto_require

_auto\_require_ is a macro implementation of Scala's require that comes with auto-generated error messages.  
It has no dependencies other than _scala-reflect_.

## Usage

```scala
libraryDependencies ++= Seq(
    "com.github.cerst" %% "auto-require" % autoRequireVersion
)
```

````scala
import com.github.cerst.autorequire._

final case class Person(age: Int, name: String) {
    autoRequire[Person](!(age < 14) && name.nonEmpty)
}

val _ = Person(10, "John")
// Exception in thread "main" [...]: Requirement failed for 'Person': '!(Person.this.age < 13) && scala.Predef.augmentString(Person.this.name).nonEmpty' { Person.this.age = 10, Person.this.name = John }
````

In order to display the error message above, the macro generates variable declarations for all variables found in the expression.     
Whereas you can use _autoRequire_ everywhere, you always need to specify a generic type as the latter is displayed in the error message.   
If you prefer not throwing exceptions, you can use _autoRequireEither_.  
There are a few ways to customize the error message. Please check the Scaladoc of _com.github.cerst.autorequire.DisplayConfig_
and its sub-types for more info.
