lazy val commonSettings: Seq[Setting[_]] = Seq(
  organization := "de.lolhens",
  version := "0.16.0",

  scalaVersion := "2.13.3",
  crossScalaVersions := Seq("2.12.12", scalaVersion.value),
  scalacOptions ++= Seq("-language:_", "-deprecation"),

  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0")),

  homepage := Some(url("https://github.com/LolHens/play2-auth")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/LolHens/play2-auth"),
      "scm:git@github.com:LolHens/play2-auth.git"
    )
  ),
  developers := List(
    Developer(id = "LolHens", name = "Pierre Kisters", email = "pierrekisters@gmail.com", url = url("https://github.com/LolHens/"))
  ),

  Compile / doc / sources := Seq.empty,

  version := {
    val tagPrefix = "refs/tags/"
    sys.env.get("CI_VERSION").filter(_.startsWith(tagPrefix)).map(_.drop(tagPrefix.length)).getOrElse(version.value)
  },

  publishMavenStyle := true,

  publishTo := sonatypePublishToBundle.value,

  credentials ++= (for {
    username <- sys.env.get("SONATYPE_USERNAME")
    password <- sys.env.get("SONATYPE_PASSWORD")
  } yield Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    username,
    password
  )).toList
)

val playVersion = play.core.PlayVersion.current

lazy val core = project.in(file("module"))
  .settings(commonSettings)
  .settings(
    name := "play2-auth",

    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % playVersion % "provided",
      "com.typesafe.play" %% "play-cache" % playVersion % "provided",
      "de.lolhens" %% "stackable-controller" % "0.7.0"
    )
  )

lazy val test = project.in(file("test"))
  .settings(commonSettings)
  .settings(
    publish / skip := true,

    name := "play2-auth-test",

    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-test" % playVersion
    )
  ).dependsOn(core)

lazy val sample = project.in(file("sample"))
  .enablePlugins(play.sbt.PlayScala)
  .settings(commonSettings)
  .settings(
    publish / skip := true,

    name := "play2-auth-sample",

    libraryDependencies ++= Seq(
      ehcache,
      jdbc,
      guice,
      specs2 % Test,
      "org.mindrot" % "jbcrypt" % "0.4",
      "com.h2database" % "h2" % "1.4.200",
      "org.scalikejdbc" %% "scalikejdbc" % "3.5.0",
      "org.scalikejdbc" %% "scalikejdbc-config" % "3.5.0",
      "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "3.5.0",
      "org.scalikejdbc" %% "scalikejdbc-test" % "3.5.0" % "test",
      "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.8.0-scalikejdbc-3.5",
      "org.scalikejdbc" %% "scalikejdbc-play-dbapi-adapter" % "2.8.0-scalikejdbc-3.5",
      "org.scalikejdbc" %% "scalikejdbc-play-fixture" % "2.8.0-scalikejdbc-3.5",
      "org.flywaydb" %% "flyway-play" % "6.0.0"
    ),
    TwirlKeys.templateImports in Compile ++= Seq(
      "jp.t2v.lab.play2.auth.sample._",
      "play.api.data.Form",
      "play.api.mvc.Flash",
      "views._",
      "views.html.helper",
      "controllers._"
    ),
    publishArtifact := false,
    routesGenerator := InjectedRoutesGenerator
  )
  .dependsOn(core, test % "test")

lazy val social = project.in(file("social"))
  .settings(commonSettings)
  .settings(
    name := "play2-auth-social",

    libraryDependencies ++= Seq(
      ws,
      "com.typesafe.play" %% "play" % playVersion % "provided",
      "com.typesafe.play" %% "play-ws" % playVersion % "provided"
    )
  ).dependsOn(core)

lazy val socialSample = project.in(file("social-sample"))
  .enablePlugins(play.sbt.PlayScala)
  .settings(commonSettings)
  .settings(
    publish / skip := true,

    name := "play2-auth-social-sample",

    resourceDirectories in Test += baseDirectory.value / "conf",

    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-ws" % playVersion,
      "com.typesafe.play" %% "play-cache" % playVersion,
      "org.scalikejdbc" %% "scalikejdbc" % "3.5.0",
      "org.scalikejdbc" %% "scalikejdbc-config" % "3.5.0",
      "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "3.5.0",
      "org.scalikejdbc" %% "scalikejdbc-test" % "3.5.0" % "test",
      "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.8.0-scalikejdbc-3.5",
      "org.scalikejdbc" %% "scalikejdbc-play-dbapi-adapter" % "2.8.0-scalikejdbc-3.5",
      "org.scalikejdbc" %% "scalikejdbc-play-fixture" % "2.8.0-scalikejdbc-3.5",
      "org.flywaydb" %% "flyway-play" % "6.0.0"
    ),
    publishArtifact := false,
    routesGenerator := InjectedRoutesGenerator
  )
  .dependsOn(core, social)

lazy val root = project.in(file("."))
  .settings(commonSettings)
  .settings(
    publish / skip := true
  )
  .aggregate(core, test, sample, social, socialSample)
