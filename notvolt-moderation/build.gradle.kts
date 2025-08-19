plugins {
	java
}

dependencies {
	implementation(project(":notvolt-common"))
	implementation(project(":notvolt-services"))
	implementation(libs.jda)
	implementation(libs.spring.boot.starter.data.jpa)
}
