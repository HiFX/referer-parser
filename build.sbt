name := "referer-parser"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= {
  object V {
    val circeYaml = "0.6.1"
    val circeGeneric = "0.8.0"
    val scalaUri = "0.5.1"
    val specs2 = "3.7"
  }
  Seq(
    "io.circe" %% "circe-yaml" % V.circeYaml,
    "io.circe" %% "circe-generic" % V.circeGeneric,
    "io.lemonlabs" %% "scala-uri" % V.scalaUri,
    "org.specs2" %% "specs2" % V.specs2 % "test"
  )
}
