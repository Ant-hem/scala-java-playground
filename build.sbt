organization := "com.algolia"

name := "scala-java-playground"

version := "0.1"

scalaVersion := "2.12.0"

val algoliaVersion = "3.0.0-beta-2"
val scalaTestVersion = "3.0.5"
val scalacheckVersion = "1.14.0"
val jacksonVersion = "2.9.8"
val scalaJavaCompatVersion = "0.9.0"

// External dependencies
libraryDependencies += "com.algolia" % "algoliasearch-core" % algoliaVersion
libraryDependencies += "com.algolia" % "algoliasearch-apache" % algoliaVersion
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion
libraryDependencies += "org.scala-lang.modules" %% "scala-java8-compat" % scalaJavaCompatVersion

// Test dependencies
libraryDependencies += "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % scalacheckVersion % "test"
