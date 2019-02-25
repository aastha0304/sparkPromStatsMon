name := "spark_metrics"

version := "0.1"

scalaVersion := "2.11.0"

val sparkVersion = "2.3.0"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.9.3"
  , "org.joda" % "joda-convert" % "1.7"

  , "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
  , "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"

  , "com.typesafe" % "config" % "1.2.1"
  , "org.scalatest" % "scalatest_2.11" % "3.0.1" % "test"
  , "io.netty" % "netty" % "3.9.9.Final" % "test"
  , "com.typesafe" % "scalalogging-slf4j_2.10" % "1.0.1"  % "test"
  , "org.slf4j" % "slf4j-api" % "1.7.16"  % "test"
  //    , "org.slf4j" % "log4j-over-slf4j" % "1.7.1"  % "test" // for any java classes looking for this
  , "io.dropwizard.metrics" % "metrics-core" % "3.1.5"
  , "org.eclipse.jetty" % "jetty-servlet" % "9.3.24.v20180605"
)