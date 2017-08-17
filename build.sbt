name := (name in ThisBuild).value

inThisBuild(Seq(
  name := "play2-auth",
  version := "0.16.0",
  scalaVersion := "2.12.3",
  organization := "org.lolhens",
  resolvers := Seq(
    "artifactory-maven" at "http://lolhens.no-ip.org/artifactory/maven-public/",
    Resolver.url("artifactory-ivy", url("http://lolhens.no-ip.org/artifactory/ivy-public/"))(Resolver.ivyStylePatterns)
  ),
  scalacOptions ++= Seq("-language:_", "-deprecation"),

  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _: MavenRepository => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := {
    <url>https://github.com/t2v/play2-auth</url>
      <licenses>
        <license>
          <name>Apache License, Version 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:t2v/play2-auth.git</url>
        <connection>scm:git:git@github.com:t2v/play2-auth.git</connection>
      </scm>
      <developers>
        <developer>
          <id>gakuzzzz</id>
          <name>gakuzzzz</name>
          <url>https://github.com/gakuzzzz</url>
        </developer>
      </developers>
  }
))

val playVersion = play.core.PlayVersion.current

lazy val core = project.in(file("module"))
  .settings(name := (name in ThisBuild).value)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % playVersion % "provided",
      "com.typesafe.play" %% "play-cache" % playVersion % "provided",
      "com.jaroop" %% "stackable-controller" % "0.7.0"
    )
  )

lazy val test = project.in(file("test"))
  .settings(
    name := (name in ThisBuild).value + "-test",
    libraryDependencies += "com.typesafe.play" %% "play-test" % playVersion
  ).dependsOn(core)

lazy val sample = project.in(file("sample"))
  .enablePlugins(play.sbt.PlayScala)
  .settings(
    name := (name in ThisBuild).value + "-sample",
    resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
    libraryDependencies ++= Seq(
      play.sbt.Play.autoImport.ehcache,
      play.sbt.Play.autoImport.specs2 % Test,
      play.sbt.Play.autoImport.jdbc,
      "org.mindrot" % "jbcrypt" % "0.3m",
      "org.scalikejdbc" %% "scalikejdbc" % "3.0.0",
      "org.scalikejdbc" %% "scalikejdbc-config" % "3.0.0",
      "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "3.0.0",
      "org.scalikejdbc" %% "scalikejdbc-test" % "3.0.0" % "test",
      "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0",
      "org.scalikejdbc" %% "scalikejdbc-play-dbapi-adapter" % "2.6.0",
      "org.scalikejdbc" %% "scalikejdbc-play-fixture" % "2.6.0",
      "org.flywaydb" %% "flyway-play" % "4.0.0"
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

lazy val social = Project(id = "social", base = file("social"))
  .settings(
    name := (name in ThisBuild).value + "-social",
    libraryDependencies ++= Seq(
      play.sbt.Play.autoImport.ws,
      "com.typesafe.play" %% "play" % playVersion % "provided",
      "com.typesafe.play" %% "play-ws" % playVersion % "provided"
    )
  ).dependsOn(core)

lazy val socialSample = Project("social-sample", file("social-sample"))
  .enablePlugins(play.sbt.PlayScala)
  .settings(
    name := (name in ThisBuild).value + "-social-sample",
    resourceDirectories in Test += baseDirectory.value / "conf",
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-ws" % playVersion,
      "com.typesafe.play" %% "play-cache" % playVersion,
      "org.flywaydb" %% "flyway-play" % "4.0.0",
      "org.scalikejdbc" %% "scalikejdbc" % "3.0.0",
      "org.scalikejdbc" %% "scalikejdbc-config" % "3.0.0",
      "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "3.0.0",
      "org.scalikejdbc" %% "scalikejdbc-test" % "3.0.0" % "test",
      "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0",
      "org.scalikejdbc" %% "scalikejdbc-play-dbapi-adapter" % "2.6.0",
      "org.scalikejdbc" %% "scalikejdbc-play-fixture" % "2.6.0"
    ),
    publishArtifact := false,
    routesGenerator := InjectedRoutesGenerator
  )
  .dependsOn(core, social)

lazy val root = project.in(file("."))
  .settings(publishArtifact := false)
  .aggregate(core, test, sample, social, socialSample)
