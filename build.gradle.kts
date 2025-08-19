import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

allprojects {
	repositories {
		mavenCentral()
		maven(url = uri("https://m2.dv8tion.net/releases"))
		maven(url = uri("https://jitpack.io"))
	}
}

subprojects {
	pluginManager.apply("java")

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(24))
		}
	}

	tasks.withType<org.gradle.api.tasks.compile.JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		options.release.set(24)
	}

	tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
		useJUnitPlatform()
		testLogging {
			events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED)
			exceptionFormat = TestExceptionFormat.FULL
		}
	}
}
