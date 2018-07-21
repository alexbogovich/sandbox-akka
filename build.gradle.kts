plugins {
    scala
}

group = "com.github.alexbogovich"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    compile ("org.scala-lang:scala-library:2.12.6")
    compile ("com.typesafe.akka:akka-stream_2.12:2.5.14")

    testCompile ("org.scalatest:scalatest_2.11:3.0.0")
    testCompile ("junit:junit:4.12")
}