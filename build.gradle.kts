import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
	java
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.4"
	id("com.google.cloud.tools.jib") version "3.4.0"
}

group = "com.reservation"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

val springDocVersion = "2.0.2"
val commonsValidatorVersion = "1.7"

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}


repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("commons-validator:commons-validator:$commonsValidatorVersion")
	implementation("org.hibernate.validator:hibernate-validator")

	// for lock registry
	implementation ("org.springframework.boot:spring-boot-starter-data-redis")
	implementation ("org.springframework.integration:spring-integration-redis")
	implementation ("io.lettuce:lettuce-core")

	// swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")

	// for mysql
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")

	// lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// for testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.h2database:h2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jib {
	from {
		image = "amazoncorretto:21-alpine-jdk"
	}
	to {
		image = project.name
	}
	container {
		jvmFlags = listOf("-Xms500m", "-Xmx500m")
	}

}

tasks.test{
	testLogging {
		events("passed", "skipped", "failed")
		showExceptions = true
		showCauses = true
		showStackTraces = true
		exceptionFormat = TestExceptionFormat.FULL

		// For verbose logging
		// showStandardStreams = true
	}
}
