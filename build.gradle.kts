plugins {
    scala
    id("com.gradle.build-scan") version "1.15.1"
}

group = "com.github.alexbogovich"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    compile("org.scala-lang:scala-library:2.12.6")
    compile("com.typesafe.akka:akka-stream_2.12:2.5.14")
    compile("com.typesafe.akka:akka-actor-typed_2.12:2.5.14")
    compile("com.typesafe.akka:akka-http_2.12:10.1.3")
    compile("com.typesafe.akka:akka-http-spray-json_2.12:10.1.3")

    testCompile("org.scalatest:scalatest_2.12:3.0.5")
    testCompile("com.typesafe.akka:akka-stream-testkit_2.12:2.5.14")
    testCompile("com.typesafe.akka:akka-actor-testkit-typed_2.12:2.5.14")

    testCompile("org.junit.platform:junit-platform-runner:1.2.0")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
}

tasks {
    withType<Wrapper> {
        gradleVersion = "4.9"
        distributionType = Wrapper.DistributionType.ALL
    }
    withType<Test> {
        useJUnitPlatform()
    }
}

buildScan {
    setTermsOfServiceUrl("https://gradle.com/terms-of-service")
    setTermsOfServiceAgree("yes")
}