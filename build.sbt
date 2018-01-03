name := "referer-parser"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= {
  object V {
    val circeYaml = "0.6.1"
  }
  Seq(
    "io.circe" %% "circe-yaml" % V.circeYaml,
    "io.circe" %% "circe-generic" % "0.8.0",
    "io.lemonlabs" %% "scala-uri" % "0.5.1",
    "org.apache.httpcomponents" % "httpclient" % "4.5.3",
    "org.specs2" %% "specs2" % "3.7" % "test"


  )
}
