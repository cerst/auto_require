# Changelog

## 0.3.4
* Update the build

## 0.3.3
* Update the build

## 0.3.2
* Update the build

## 0.3.1
* Fix an incorrect macro assumption which treated unary methods as field selections thus printing the respective tree 
  instead further deconstructing it (e.g. 'name.nonEmpty' resulted in a printed variable 
  'scala.Predef.augmentString(name).nonEmpty = true' instead of only printing the name)
* Not published due to an issue during publishing - please use 0.3.2

## 0.3.0
* Change artifact name to _auto-require_ ('-' rather than '_')
* Change package name to _autorequire_ (remove '_')

## 0.2.0
* Error messages
  * no longer contain evaluated sub-expressions (but still all variables)
  * are formatted as a single line (no more line breaks or indentation)

## 0.1.0
* release first version
