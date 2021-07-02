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
    libraryDependencies ++= commonDependencies ++ Seq( dependencies.e4s
    )
  )
  .dependsOn(
    common
  )

// DEPENDENCIES

lazy val dependencies =
  new {
    val logbackV        = "1.2.3"
    val logstashV       = "4.11"
    val typesafeConfigV = "1.3.1"
    val akkaHttpV       = "10.2.4"
    val scalatestV      = "3.0.4"
    val scalacheckV     = "1.13.5"
    val elastic4sVersion = "7.12.3"

    val logback        = "ch.qos.logback"             % "logback-classic"          % logbackV
    val logstash       = "net.logstash.logback"       % "logstash-logback-encoder" % logstashV
    val typesafeConfig = "com.typesafe"               % "config"                   % typesafeConfigV
    val akkaHttp           = "com.typesafe.akka"          %% "akka-http"             % akkaHttpV
    val scalatest      = "org.scalatest"              %% "scalatest"               % scalatestV
    val scalacheck     = "org.scalacheck"             %% "scalacheck"              % scalacheckV

     val e4s =  "com.sksamuel.elastic4s" %% "elastic4s-client-esjava" % elastic4sVersion
      val e4st =  "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elastic4sVersion % "test"
  }

lazy val commonDependencies = Seq(
  dependencies.logback,
  dependencies.logstash,
  dependencies.typesafeConfig,
  dependencies.akkaHttp,
  dependencies.scalatest  % "test",
  dependencies.scalacheck % "test",
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
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)



