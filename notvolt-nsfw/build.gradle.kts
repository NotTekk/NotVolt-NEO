plugins {
	java
}

dependencies {
	implementation(project(":notvolt-common"))
	implementation(project(":notvolt-services"))
	implementation(project(":notvolt-integrations"))
	implementation(libs.jda)
	implementation(libs.jackson.databind)
}
