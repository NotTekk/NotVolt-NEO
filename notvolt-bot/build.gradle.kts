plugins {
	application
	java
}

dependencies {
	implementation(project(":notvolt-common"))
	implementation(project(":notvolt-services"))
	implementation(project(":notvolt-moderation"))
	implementation(project(":notvolt-music"))
	implementation(project(":notvolt-nsfw"))
	implementation(project(":notvolt-utility"))
	implementation(project(":notvolt-integrations"))

	implementation(libs.jda)
	runtimeOnly(libs.slf4j.simple)

	implementation(libs.spring.boot.starter.data.jpa)
	runtimeOnly(libs.postgres)
	implementation(libs.lettuce)
}

application {
	mainClass.set("dev.nottekk.notvolt.bot.BotLauncher")
}
