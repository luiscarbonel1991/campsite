plugins {
	java
	id("org.springframework.boot") version "3.0.1"
	id("io.spring.dependency-management") version "1.1.0"
	id("io.freefair.lombok") version "6.6.1"
}

group = "com.reservation"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val shedlockVersion = "4.28.0"
val redisVersion = "6.0.1"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("commons-validator:commons-validator:1.7")
	implementation("org.hibernate.validator:hibernate-validator")

	// for lock registry
	implementation ("org.springframework.boot:spring-boot-starter-data-redis")
	implementation ("org.springframework.integration:spring-integration-redis")
	implementation ("io.lettuce:lettuce-core")
	implementation("net.javacrumbs.shedlock:shedlock-spring:$shedlockVersion")
	implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:$shedlockVersion")

	// for mysql
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")

	// lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// for testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
