plugins {
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.dependency.management)
	java
}

dependencies {
	implementation(project(":notvolt-common"))
	implementation(project(":notvolt-services"))
	implementation(project(":notvolt-persistence"))

	implementation(libs.spring.boot.starter.web)
	implementation(libs.spring.boot.starter.security)
	implementation(libs.spring.boot.starter.oauth2.client)
	implementation(libs.spring.boot.starter.data.jpa)
	implementation(libs.spring.boot.starter.validation)
	implementation(libs.spring.boot.starter.actuator)
	implementation(libs.springdoc.openapi)
	implementation(libs.flyway.core)
	runtimeOnly(libs.postgres)
	implementation(libs.okhttp)
	implementation("io.micrometer:micrometer-registry-prometheus")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(24))
	}
}
