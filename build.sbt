name := "common-tools"

version := "1" 



libraryDependencies += "org.seleniumhq.selenium" % "selenium-chrome-driver" % "2.37.1" % "test"

libraryDependencies += "org.specs2" % "specs2_2.10" % "2.3.7" % "test"

libraryDependencies += "com.asksunny.commons" % "utils" % "0.0.1"

libraryDependencies += "junit" % "junit" % "4.11" % "test"

libraryDependencies += "log4j" % "log4j" % "1.2.15"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.5"

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.5"

libraryDependencies += "commons-codec" % "commons-codec" % "1.8"






resolvers += "Local Maven Repository" at "file:/C:/Users/Sunny%20Liu/.m2/repository/"

resolvers += "typesafe_repo" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "oss.sonatype.org" at "http://oss.sonatype.org/content/repositories/releases"

resolvers += "sonatype-snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

resolvers += "central" at "http://repo1.maven.org/maven2"

