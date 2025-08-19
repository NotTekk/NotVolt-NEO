pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
	}
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
	repositories {
		mavenCentral()
		maven(url = uri("https://m2.dv8tion.net/releases"))
		maven(url = uri("https://jitpack.io"))
	}
	versionCatalogs {
		create("libs") {
			from(files("gradle/libs.versions.toml"))
		}
	}
}

rootProject.name = "notvolt"

include(
	"notvolt-common",
	"notvolt-persistence",
	"notvolt-services",
	"notvolt-integrations",
	"notvolt-moderation",
	"notvolt-music",
	"notvolt-nsfw",
	"notvolt-utility",
	"notvolt-image",
	"notvolt-bot",
	"notvolt-web"
)
