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
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
