plugins {
    kotlin("jvm") version "2.2.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
}

group = "me.yapoo.computer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("io.kotest:kotest-bom:5.9.1"))
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.kotest:kotest-property")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
