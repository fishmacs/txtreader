androidBuild

resolvers ++= Resolver.typesafeRepo("snapshots") :: Nil

javacOptions in Compile ++= "-source" :: "1.7" :: "-target" :: "1.7" :: Nil

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "4.2",
  // sqlite
  // "org.xerial" % "sqlite-jdbc" % "3.8.11.2",
  "org.sqldroid" % "sqldroid" % "1.0.3",
  "io.getquill" %% "quill-jdbc" % "0.8.0",
  //"com.readystatesoftware.sqliteasset" % "sqliteassethelper" % "2.0.1",
  // test
  "org.robolectric" % "robolectric" % "3.0" % "test",
  //"org.apache.maven" % "maven-ant-tasks" % "2.1.3" % "test",
  //"com.novocode" % "junit-interface" % "0.11" % "test",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "org.scalactic" %% "scalactic" % "2.2.6",
  "com.geteit" %% "robotest" % "0.12" % Test
)

fork in Test := true

unmanagedClasspath in Test ++= (bootClasspath in Android).value

//protifySettings

proguardOptions in Android ++= Seq(
  "-dontwarn org.scaloid.common.TraitWebView*",
  "-dontwarn org.scalactic.**",
  "-dontwarn org.slf4j.**",
  "-dontwarn com.zaxxer.**",
  "-dontwarn com.typesafe.config.**",
//  "-keep public class org.sqldroid.**",
//  "-keep public class org.scaloid.**",
  "-keep public class scala.collection.mutable.BitSet"
)

transitiveClassifiers in Global := Seq()

retrolambdaEnabled in Android := true

dexMulti in Android := true

dexMinimizeMain in Android := true

dexMainClasses in Android := IO.readLines(baseDirectory.value / "MainDexList.txt")
