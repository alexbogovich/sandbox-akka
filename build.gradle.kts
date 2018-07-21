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

    testCompile ("org.scalatest:scalatest_2.12:3.0.5")
    testCompile ("com.typesafe.akka:akka-stream-testkit_2.12:2.5.14")
    testCompile ("junit:junit:4.12")
}

task<Wrapper>("wrapper") {
    gradleVersion = "4.9"
    distributionType = Wrapper.DistributionType.ALL
}