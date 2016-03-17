name := "dstream-zeromq"

organization := "org.spark-project"

scalaVersion in ThisBuild := "2.11.7"

crossScalaVersions in ThisBuild := Seq("2.10.5", "2.11.7")

spName := "org.spark-project/dstream-zeromq"

sparkVersion in ThisBuild := "2.0.0-SNAPSHOT"

val testSparkVersion = settingKey[String]("The version of Spark to test against.")

testSparkVersion in ThisBuild := sys.props.getOrElse("spark.testVersion", sparkVersion.value)

spAppendScalaVersion := true

spIncludeMaven := true

spIgnoreProvided := true

sparkComponents in ThisBuild := Seq("streaming")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-zeromq" % "2.3.11",
  "org.spark-project" %% "dstream-akka" % "0.0.1",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "org.apache.spark" %% "spark-core" % testSparkVersion.value % "test" classifier "tests"
)

// Display full-length stacktraces from ScalaTest:
testOptions in Test += Tests.Argument("-oF")
// Display the java unit tests in console
testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-v")

ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := {
  if (scalaBinaryVersion.value == "2.10") false
  else true
}

val root = project in file(".")

val examples = project in file("examples") dependsOn (root % "compile->compile") settings (
  libraryDependencies ++= Seq(
    // Explicitly declare them to run examples using run-main.
    "org.apache.spark" %% "spark-core" % testSparkVersion.value,
    "org.apache.spark" %% "spark-streaming" % testSparkVersion.value
  )
)

// Remove this once Spark 2.0.0 is out
resolvers in ThisBuild += "apache-snapshots" at "https://repository.apache.org/snapshots/"

resolvers in ThisBuild += "Databricks Bintray" at "https://bintray.com/artifact/download/databricks/maven/"

/********************
 * Release settings *
 ********************/

publishMavenStyle := true

releaseCrossBuild := true

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

releasePublishArtifactsAction := PgpKeys.publishSigned.value

pomExtra :=
  <url>https://github.com/spark-packages/dstream-zeromq</url>
  <scm>
    <url>git@github.com:spark-packages/dstream-zeromq.git</url>
    <connection>scm:git:git@github.com:spark-packages/dstream-zeromq.git</connection>
  </scm>
  <developers>
    <developer>
      <id>marmbrus</id>
      <name>Michael Armbrust</name>
      <url>https://github.com/marmbrus</url>
    </developer>
    <developer>
      <id>tdas</id>
      <name>Tathagata Das</name>
      <url>https://github.com/tdas</url>
    </developer>
    <developer>
      <id>zsxwing</id>
      <name>Shixiong Zhu</name>
      <url>https://github.com/zsxwing</url>
    </developer>
  </developers>

bintrayReleaseOnPublish in ThisBuild := false

import ReleaseTransformations._

// Add publishing to spark packages as another step.
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges,
  releaseStepTask(spPublish)
)
