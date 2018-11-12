name := "referer-parser"

version := "0.8"

scalaVersion := "2.11.12"

libraryDependencies ++= {
  object V {
    val circeYaml = "0.6.1"
    val circeGeneric = "0.8.0"
    val jUrl = "v0.3.0"
    val specs2 = "3.7"
  }
  Seq(
    "io.circe" %% "circe-yaml" % V.circeYaml,
    "io.circe" %% "circe-generic" % V.circeGeneric,
    "com.github.anthonynsimon" %% "jurl" % V.jUrl,
    "org.specs2" %% "specs2" % V.specs2 % "test"
  )
}
