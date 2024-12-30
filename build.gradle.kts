import org.jetbrains.kotlin.gradle.dsl.JvmTarget

import org.springframework.boot.gradle.tasks.run.BootRun
import java.util.Properties

tasks.named<BootRun>("bootRun") {
    doFirst {
        val envFile = file(".env.properties")
        if (envFile.exists()) {
            val props = Properties()
            envFile.inputStream().use { props.load(it) }

            props.forEach { key, value ->
                environment(key.toString(), value.toString())
            }
        }
    }
}

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("plugin.jpa") version "2.1.0"

    // Docker-compose plugin
        id("com.avast.gradle.docker-compose") version "0.16.12"
}

group = "com.hvs"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_23)
    }
}

dockerCompose {
    useComposeFiles = listOf("docker-compose.yml")
    executable = "/usr/local/bin/docker" // Full path to Docker executable
    startedServices = listOf("postgres")           // Specify the service(s) you want to start
    stopContainers = true                          // Stops containers after the build
    removeContainers = false                       // Keeps containers after stopping
    removeVolumes = false                          // Keeps volumes after stopping
}


repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")


    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Kotlin logger
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    // Postgres
    runtimeOnly("org.postgresql:postgresql")

    // LiquiBase
    implementation("org.liquibase:liquibase-core:4.30.0")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
