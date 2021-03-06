import scala.xml._
import java.io._

object MavenPomMigrater {

def using[A <: {def close(): Unit}, B](param: A)(f: A => B): B =
try { f(param) } finally { param.close() }

def writeToFile(fileName:String, data:String) = 
  using (new FileWriter(fileName)) {
    fileWriter => fileWriter.write(data)
  }

def appendToFile(fileName:String, textData:String) =
  using (new FileWriter(fileName, true)){ 
    fileWriter => using (new PrintWriter(fileWriter)) {
      printWriter => printWriter.println(textData)
    }
 }

 def main(args: Array[String]){
  
val javaMainSrc = new File("src/main/java")
javaMainSrc.mkdirs()
val javaTestSrc = new File("src/test/java")
javaTestSrc.mkdirs()

val scalaMainSrc = new File("src/main/scala")
scalaMainSrc.mkdirs()
val scalaTestSrc = new File("src/test/scala")
scalaTestSrc.mkdirs()

val resourcesMainSrc = new File("src/main/resources")
resourcesMainSrc.mkdirs()
val resourcesTestSrc = new File("src/test/resources")
resourcesTestSrc.mkdirs()

val emptyPom = """<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>you.group.id</groupId>
	<artifactId>your-project-name</artifactId>
	<version>1.0-SNAPSHOT</version>
	<repositories>

		<repository>
			<id>typesafe_repo</id>
			<name>typesafe_repo</name>
			<releases>
				<enabled>true</enabled>
			</releases>
			<url>http://repo.typesafe.com/typesafe/releases/</url>
		</repository>
		<repository>
			<id>oss.sonatype.org</id>
			<name>releases</name>
			<url>http://oss.sonatype.org/content/repositories/releases</url>
		</repository>
		<repository>
			<id>sonatype-snapshots</id>
			<name>snapshots</name>
			<url>http://oss.sonatype.org/content/repositories/snapshots</url>
		</repository>
	</repositories>
	<dependencies>
		
		<dependency>
			<groupId>org.seleniumhq.selenium</groupId>
			<artifactId>selenium-chrome-driver</artifactId>
			<version>2.37.1</version>
			<scope>test</scope>			
		</dependency>

		<dependency>
			<groupId>org.specs2</groupId>
			<artifactId>specs2_2.10</artifactId>
			<version>2.3.7</version>
			<scope>test</scope>			
		</dependency>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.11</version>
			<scope>test</scope>
		</dependency>
		
		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>1.2.15</version>
			<exclusions>
				<exclusion>
					<artifactId>jmxtools</artifactId>
					<groupId>com.sun.jdmk</groupId>
				</exclusion>
				<exclusion>
					<artifactId>jmxri</artifactId>
					<groupId>com.sun.jmx</groupId>
				</exclusion>
				<exclusion>
					<artifactId>jms</artifactId>
					<groupId>javax.jms</groupId>
				</exclusion>
				<exclusion>
					<artifactId>mail</artifactId>
					<groupId>javax.mail</groupId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>1.7.2</version>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-log4j12</artifactId>
			<version>1.7.2</version>
		</dependency>
     </dependencies>
</project>
"""
val pomFile = new File("pom.xml")
if(!pomFile.exists())
	writeToFile("pom.xml", emptyPom)
	
	
val projectDir = new File("project")
projectDir.mkdirs()

val gitIgnore = """logs
project/project
project/target
target
tmp
.history
dist
/.idea
/*.iml
/out
/.idea_modules
/.classpath
/.project
/RUNNING_PID
/.settings
"""
writeToFile(".gitignore", gitIgnore)

val pomXML = XML.load("pom.xml")

val partifactId = "\"".concat((pomXML \ "artifactId").text).concat("\"");
val pversion = "\"".concat((pomXML \ "version").text).concat("\"");


var projectBuild = f"""name := $partifactId%s

version := $pversion%s 



"""

  
( pomXML \\ "dependencies") \ "dependency" foreach ((dependency: Node) => {
val groupId = (dependency \ "groupId").text
val artifactId = (dependency \ "artifactId").text
val version = (dependency \ "version").text
val scope = (dependency \ "scope").text
val classifier = (dependency \ "classifier").text
val artifactValName: String = artifactId.replaceAll("[-\\.]", "_")

val dep = ("libraryDependencies += \"%s\" %% \"%s\" %% \"%s\"".format(groupId, artifactId, version))
val tscope = scope match {
  case "" => ("\n\n")
  case _ => (" %% \"%s\"\n\n".format(scope))
}
projectBuild = projectBuild.concat(dep).concat(tscope)

None
});
projectBuild = projectBuild.concat("\n\n\n\n\n")

val mavenLocal = (new File(System.getProperty("user.home"), ".m2/repository")).toURI.toURL.toString

projectBuild = projectBuild.concat("resolvers += \"Local Maven Repository\" at \"%s\"\n\n".format(mavenLocal))



(pomXML \\ "repositories") \ "repository" foreach ((repository: Node) => {
val id = (repository \ "id").text
val url = (repository \ "url").text

projectBuild = projectBuild.concat("resolvers += \"%s\" at \"%s\"\n\n".format(id, url))
None
});


writeToFile("build.sbt", projectBuild) 


val buildProp = """sbt.version=0.13.1
"""
writeToFile("project/build.properties", buildProp) 


val pluginDef = """// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-pom-reader" % "1.0.1")

"""
writeToFile("project/plugins.sbt", pluginDef) 

}

}
