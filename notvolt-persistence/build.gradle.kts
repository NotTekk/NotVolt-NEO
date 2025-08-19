plugins {
	java
}

dependencies {
	implementation(project(":notvolt-common"))
	api(libs.spring.boot.starter.data.jpa)
}
