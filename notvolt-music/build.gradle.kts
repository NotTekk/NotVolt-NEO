plugins {
	java
}

dependencies {
	implementation(project(":notvolt-common"))
	implementation(project(":notvolt-services"))
	implementation(libs.jda)
	implementation(libs.jackson.databind)
	// TODO: add lavalink client dependency when selecting a stable Java client
}
