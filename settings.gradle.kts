
rootProject.name = "helloworld-ms"
include("app")

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// We must use environment variables for security reasons, and to allow
// the credentials to be passed to docker containers running from pipelines.
// REPSY_USERNAME must be defined as an environment variable
val repsyUsername = System.getenv("REPSY_USERNAME")

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://repo.repsy.io/mvn/rubensgomes/default/")
            credentials {
                username = repsyUsername
            }
        }
    }

    versionCatalogs {
        create("ctlg") {
            from("com.rubensgomes:gradle-catalog:0.0.42")
        }
    }
}

