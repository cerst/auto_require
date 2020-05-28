lazy val root = (project in file("."))
  .aggregate(core)
  .settings(
    // root intentionally does not contain any code, so don't publish
    ReleaseSettings.disabled,
    // crossScalaVersions must be set to Nil on the aggregating project
    // https: //www.scala-sbt.org/1.x/docs/Cross-Build.html#Cross+building+a+project
    crossScalaVersions := Nil,
    name := "auto-require-root"
  )

lazy val core = (project in file("core"))
  .settings(
    ReleaseSettings.libraryOptimized("com.github.cerst.autorequire"),
    crossScalaVersions := CommonValues.crossScalaVersions,
    libraryDependencies ++= Dependencies.core(scalaVersion.value),
    name := "auto-require",
    scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      // required for tests with newtype
      // https://github.com/estatico/scala-newtype
      case Some((2, 13)) => Seq("-Ymacro-annotations")
      case _             => Seq()
    })
  )
