
rootProject.name = "helloworld-ms"
include("app")

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// We must use environment variables for security reasons, and also to allow
// the credentials to be passed to docker containers running from pipelines.
// NOTE: these variable must be defined in a CircleCI context used by the build.
// REPSY_USERNAME and exitREPSY_PASSWORD environment variables
val repsyUsername = System.getenv("REPSY_USERNAME")
val repsyPassword = System.getenv("REPSY_PASSWORD")

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://repo.repsy.io/mvn/rubensgomes/default/")
            credentials {
                username = repsyUsername
                password = repsyPassword
            }
        }
    }

    versionCatalogs {
        create("ctlg") {
            from("com.rubensgomes:gradle-catalog:0.0.42")
        }
    }
}

