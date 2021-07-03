name := "one-akka-elastic-demo"
organization in ThisBuild := "org.iv"
scalaVersion in ThisBuild := "2.12.10"
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
    libraryDependencies ++= commonDependencies ++ Seq(
    )
  )
  .dependsOn(
    common
  )

lazy val dao = project
  .settings(
    name := "dao",
    settings,
    libraryDependencies ++= commonDependencies ++ Seq(deps.e4s, deps.akkaSprayJson
    )
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
    val scalatestV = "3.0.4"
    val scalacheckV = "1.13.5"
    val elastic4sVersion = "7.12.3"

    val logback = "ch.qos.logback" % "logback-classic" % logbackV
    val logstash = "net.logstash.logback" % "logstash-logback-encoder" % logstashV
    val typesafeConfig = "com.typesafe" % "config" % typesafeConfigV
    val akkaHttp = "com.typesafe.akka" %% "akka-http" % akkaHttpV
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaV

    val scalatest = "org.scalatest" %% "scalatest" % scalatestV
    val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckV
    val akkaSprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV
    val e4s = "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % elastic4sVersion
    val e4st = "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elastic4sVersion % "test"
  }

lazy val commonDependencies = Seq(
  deps.logback,
  deps.logstash,
  deps.typesafeConfig,
  deps.akkaHttp,
  deps.akkaStream,
  deps.scalatest % "test",
  deps.scalacheck % "test",
  "org.scala-lang" % "scala-reflect" % "2.12.10"

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
  "-Yrangepos", // required by SemanticDB compiler plugin
  "-Ywarn-unused-import" // required by `RemoveUnused` rule
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  addCompilerPlugin(scalafixSemanticdb),
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)



