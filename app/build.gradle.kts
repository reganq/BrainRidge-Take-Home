/*
 * File generated from start.spring.io
*/

plugins {
    java
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "org.app"
version = "0.0.1-SNAPSHOT"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit test framework.
    testImplementation(libs.junit)

    // Spring dependency
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

//application {
    // Define the main class for the application.
//    mainClass = "org.app.bank"
//}

tasks.withType<Test> {
	useJUnitPlatform()
}
