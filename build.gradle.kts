import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.20"
	kotlin("plugin.spring") version "1.5.20"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springdoc:springdoc-openapi-data-rest:1.5.9")
	implementation("org.springdoc:springdoc-openapi-ui:1.5.9")
	implementation("org.springdoc:springdoc-openapi-kotlin:1.5.9")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	val coroutines_version = "1.5.1"
	implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
	implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutines_version")
	implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutines_version")
	implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$coroutines_version")
	implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-debug:$coroutines_version")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
