plugins {
	java
}

dependencies {
	implementation(project(":notvolt-common"))
	implementation(project(":notvolt-persistence"))
	implementation(libs.spring.boot.starter.data.jpa)
	implementation(libs.jackson.databind)
	implementation(libs.okhttp)
	implementation(libs.lettuce)

	testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
}
