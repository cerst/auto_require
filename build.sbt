lazy val root = (project in file("."))
  .aggregate(core)
  .settings(
    // root intentionally does not contain any code, so don't publish
    ReleaseSettings.disabled,
    name := "auto-require-root"
  )

lazy val core = (project in file("core"))
  .settings(libraryDependencies ++= Dependencies.core, name := "auto-require")
