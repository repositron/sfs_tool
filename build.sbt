name := "sfs_tool"
 
version := "1.0" 
      
lazy val `sfs_tool` = (project in file(".")).enablePlugins(PlayScala)

      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.13.5"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice,
  "com.lihaoyi" %% "os-lib" % "0.7.8",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test)
      