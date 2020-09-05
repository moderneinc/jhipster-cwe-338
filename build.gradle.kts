import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "1.4.0"
}

group = "io.moderne"

application {
    mainClass.set("io.moderne.cwe338.Main")
}

repositories {
    mavenLocal()
    maven { url = uri("https://dl.bintray.com/openrewrite/maven") }
    mavenCentral()
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, TimeUnit.SECONDS)
        cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
    }
}

dependencies {
    compileOnly("com.google.cloud.functions:functions-framework-api:latest.release")

    implementation("org.openrewrite:rewrite-java-11:latest.integration")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("io.javalin:javalin:latest.release")
    implementation("ch.qos.logback:logback-classic:1.0.13")

    testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.junit.jupiter:junit-jupiter-api:latest.release")
    testImplementation("org.junit.jupiter:junit-jupiter-params:latest.release")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:latest.release")

    testImplementation("org.openrewrite:rewrite-test:latest.integration")

    testImplementation("org.assertj:assertj-core:latest.release")
}

tasks.withType(KotlinCompile::class.java).configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    jvmArgs = listOf("-XX:+UnlockDiagnosticVMOptions", "-XX:+ShowHiddenFrames")
}
