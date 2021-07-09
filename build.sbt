name := "one-akka-elastic-demo"
organization in ThisBuild := "org.iv"
val scalaV = "2.13.6"
scalaVersion in ThisBuild := scalaV
version := "0.1"

// PROJECTS

lazy val global = project
  .in(file("."))
  .settings(settings)
  .aggregate(
    common,
    service,
    dao
  )

lazy val common = project
  .settings(
    name := "common",
    settings,
    libraryDependencies ++= commonDependencies
  )

lazy val service = project
  .settings(
    name := "service",
    settings,
    libraryDependencies ++=  Seq(deps.scalaMock) ++ commonDependencies
  )
  .dependsOn(
    common
  )

lazy val dao = project
  .settings(
    name := "dao",
    settings,
    libraryDependencies ++= commonDependencies ++ Seq(deps.e4s,deps.e4st)
  )
  .dependsOn(
    common
  )

// DEPENDENCIES

lazy val deps =
  new {
    val logbackV = "1.2.3"
    val logstashV = "4.11"
    val typesafeConfigV = "1.3.1"
    val akkaHttpV = "10.2.4"
    val akkaV = "2.6.13"
    val scalatestV = "3.2.9"
    val scalacheckV = "1.15.2"
    val elastic4sVersion = "7.12.3"

    val logback = "ch.qos.logback" % "logback-classic" % logbackV
    val logstash = "net.logstash.logback" % "logstash-logback-encoder" % logstashV
    val typesafeConfig = "com.typesafe" % "config" % typesafeConfigV
    val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpV
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaV

    val scalatest = "org.scalatest" %% "scalatest" % scalatestV
    val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckV
    val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV
    val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaV
    val akkaSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV
    val e4s = "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % elastic4sVersion
    val e4st = "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elastic4sVersion % "test"
    val scalaReflect = "org.scala-lang" % "scala-reflect" % scalaV
    val scalaMock = "org.scalamock" %% "scalamock" % "5.1.0" % Test

  }

lazy val commonDependencies = Seq(
  deps.logback,
  deps.logstash,
  deps.typesafeConfig,
  deps.akkaHttp,
  deps.akkaStream,
  deps.scalatest % "test",
  deps.scalacheck % "test",
  deps.scalaReflect,
  deps.akkaSprayJson,
  deps.akkaTestkit % "test",
  deps.akkaHttpTestkit % "test"
)

// SETTINGS

lazy val settings =
  commonSettings

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:experimental.macros",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8",
  "-Yrangepos"//, // required by SemanticDB compiler plugin
//  "-Ywarn-unused-import" // required by `RemoveUnused` rule
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  addCompilerPlugin(scalafixSemanticdb),
  addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.0" cross CrossVersion.full),
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    "scala reflect " at "https://mvnrepository.com/artifact/"
  )
)



