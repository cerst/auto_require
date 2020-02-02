// http://get-coursier.io/
// fast(er) dependency resolution and fetching
// useful commands:
//    coursierDependencyTree - render a dependency tree including eviction information (printed yellow)
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.3")

// https://github.com/dwijnand/sbt-dynver
// derive and set project versions based on Git meta data
addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.0.0")

// https://github.com/sbt/sbt-ghpages
// publish documentation via Github pages
// useful commands:
//    ghpagesPushSite - copies and pushes documentation into the Github pages branch
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.3")

// https://github.com/sbt/sbt-header
// generate and update source code license headers
// useful commands:
//    headerCreate
addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.4.0")

// https://github.com/sbt/sbt-license-report
// generate a list of licenses for all dependencies
// useful commands:
//    dumpLicenseReport
addSbtPlugin("com.typesafe.sbt" % "sbt-license-report" % "1.2.0")

// https://github.com/sbt/sbt-native-packager
// create an executable (e.g. thin-jar, native bundle, docker-container)
// useful commands:
//    universal:stage
//    docker:publish[Local]
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.6.1")

// https://github.com/lightbend/paradox
// build documentation website using Markdown with support for build values and code snippets
// useful commands:
//    paradox - generate documentation site into target/paradox/site/main
//    paradoxBrowse - run paradox and open browser
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.6.8")

// https://github.com/sbt/sbt-pgp
// pgp-sign releases
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.1")

// https://scalameta.org/scalafmt/
// thorough code formatting (recommendation: use Intellij plugin configured as format-on-save)
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.3.1")

// https://www.scala-sbt.org/sbt-site/index.html
// integration requirement for sbt-ghpages
// allows for keeping only the latest documentation in the main branch while not deleting old documentation
// useful commands
//    makeSite - generate documentation site into target/site
//    previewSite - generate documentation and make it available at localhost:4000
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.4.0")

// https://github.com/xerial/sbt-sonatype
// publish artifacts to Sonatype
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.8.1")

// https://github.com/rtimush/sbt-updates
// check for new releases of plugins and dependencies
// when updating this version in the scala-base repo,
// don't forget to change <project-root>/project/project/sbt-updates.sbt as well
// useful commands
//    dependencyUpdates - list possible updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.0")
