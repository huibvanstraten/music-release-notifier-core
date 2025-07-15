import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm")           version "2.1.0"
	kotlin("plugin.spring") version "2.1.0"
	kotlin("plugin.jpa")    version "2.1.0"
	kotlin("plugin.serialization") version "2.1.0"

	`java-library`
	id("io.spring.dependency-management") version "1.1.6"
}

repositories {
	mavenCentral()
	gradlePluginPortal()
}

java {
	withSourcesJar()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.0")
	}
}

dependencies {
	api(kotlin("stdlib"))
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")


	api("org.springframework.boot:spring-boot-starter-data-jpa")
	api("org.springframework.boot:spring-boot-autoconfigure")

	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")

	// RabbitMQ
	implementation("com.rabbitmq:amqp-client:5.21.0")

	// Kafka
	implementation("org.springframework.kafka:spring-kafka:4.0.0-M2")

	// Kotlin logger
	implementation("io.github.microutils:kotlin-logging:3.0.5")

	// Jackson
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("io.cloudevents:cloudevents-core:4.0.1")
	implementation("io.cloudevents:cloudevents-json-jackson:4.0.1")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.mockito:mockito-core:5.14.2")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
	testImplementation("com.squareup.okhttp3:mockwebserver:5.0.0-alpha.14")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_21)
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
