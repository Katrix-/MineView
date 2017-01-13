lazy val commonSettings = Seq(
	name := s"MineView-${thisProject.value.id}",
	organization := "net.katsstuff",
	version := "1.0",
	scalaVersion := "2.11.8",

	libraryDependencies += "net.katsstuff" %%% "typenbt" % "0.1",
	libraryDependencies += "org.spire-math" %%% "spire" % "0.11.0",
	libraryDependencies += "com.lihaoyi" %%% "autowire" % "0.2.5",
	libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.4.3"
)

lazy val shared = crossProject.crossType(CrossType.Pure).settings(commonSettings: _*).settings(
	libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % Provided
)
lazy val sharedJVM = shared.jvm
lazy val sharedJS = shared.js

lazy val webClient = project.enablePlugins(ScalaJSPlugin).dependsOn(sharedJS).settings(commonSettings: _*).settings(
	persistLauncher := true,
	skip in packageJSDependencies := false,
	jsDependencies += RuntimeDOM,
	jsDependencies += "org.webjars" % "jquery" % "2.1.3" / "2.1.3/jquery.js",
	//jsDependencies += "org.webjars.npm" % "gl-matrix" % "2.3.1" / "2.3.1/src/gl-matrix.js",
	jsDependencies += "org.webjars.bower" % "three.js" % "0.74.0" / "0.74.0/three.js",

	libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0",
	libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.9.1",

	resolvers += sbt.Resolver.bintrayRepo("denigma", "denigma-releases"),
	libraryDependencies += "org.denigma" %%% "threejs-facade" % "0.0.74-0.1.7"
)