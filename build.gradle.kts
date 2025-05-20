plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "1.5.31"
    id("io.ktor.plugin") version "3.1.0"
}

group = "hu.bme.aut"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-netty:3.1.0")
    implementation("io.ktor:ktor-server-core:3.0.0")
    implementation("io.ktor:ktor-server-call-logging:3.0.0")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("ch.qos.logback:logback-classic:1.4.14")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

ktor {
    fatJar {
        archiveFileName.set("fat.jar")
    }
}

application {
    mainClass.set("hu.bme.aut.MainKt")
}