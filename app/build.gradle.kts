plugins {
  id("distribution")
  id("idea")
  id("jacoco")
  id("java")
  `jvm-test-suite`
  id("maven-publish")
  id("version-catalog")
  // org.jetbrains.kotlin.jvm
  alias(ctlg.plugins.kotlin.jvm)
  // org.jetbrains.kotlin.plugin.spring
  alias(ctlg.plugins.kotlin.spring)
  // net.researchgate.release
  alias(ctlg.plugins.release)
  // org.sonarqube
  alias(ctlg.plugins.sonarqube)
  // com.diffplug.spotless
  alias(ctlg.plugins.spotless)
  // org.springframework.boot
  alias(ctlg.plugins.spring.boot)
  // io.spring.dependency-management
  alias(ctlg.plugins.spring.dependency.management)
}

val artifact: String by project
val developerId: String by project
val developerName: String by project
val title: String by project

configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }

// --------------- >>> dependencies <<< ---------------------------------------

dependencies {
  // ########## compileOnly ####################################################

  // ########## implementation #################################################
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  // io.swagger.core.v3:swagger-annotations
  implementation(ctlg.swagger.annotations)
  // jakarta.validation:jakarta.validation-api
  implementation(ctlg.jakarta.validation.api)

  // org.jetbrains.kotlin:kotlin-reflect
  //   -  required by Spring Boot to introspect the code  at runtime
  implementation(ctlg.kotlin.reflect)
  // org.jetbrains.kotlin:kotlin-stdlib
  implementation(ctlg.kotlin.stdlib)
  // com.fasterxml.jackson.module:jackson-module-kotlin
  //  - used to serialize/de-serialize kotlin object
  //   * not used in this project
  //  implementation(ctlg.jackson.module.kotlin)

  // ########## developmentOnly #################################################
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  developmentOnly("org.springframework.boot:spring-boot-docker-compose")

  // ########## testImplementation #############################################
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  // io.mockk:mockk
  //   - mock function (e.g., every {...}) during unit testing
  testImplementation(ctlg.mockk)
  // com.ninja-squad:springmockk
  //   - mock Spring Beans (e.g., @MockBean) during unit testing
  testImplementation(ctlg.springmockk)
  // bundle engine that implements the jakarta API validation
  testImplementation(ctlg.bundles.jakarta.bean.validator)
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle Base Plugin <<< ---------------------------------
// ----------------------------------------------------------------------------

tasks.check { dependsOn("sonar") }

// ----------------------------------------------------------------------------
// --------------- >>> Gradle distribution plugin <<< -------------------------
// ----------------------------------------------------------------------------

distributions { main { distributionBaseName = "helloworld-ms" } }

// ----------------------------------------------------------------------------
// --------------- >>> Gradle jacoco Plugin <<< -------------------------------
// ----------------------------------------------------------------------------

tasks.jacocoTestReport {
  // tests are required to run before generating the report
  dependsOn(tasks.test)
}

tasks.jacocoTestReport {
  reports {
    xml.required = true
    csv.required = false
    html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
  }
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle idea Plugin <<< ---------------------------------
// ----------------------------------------------------------------------------

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle java Plugin <<< ---------------------------------
// ----------------------------------------------------------------------------

java {
  withSourcesJar()
  withJavadocJar()
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
    vendor.set(JvmVendorSpec.AMAZON)
  }
}

tasks.jar {
  manifest {
    attributes(
        mapOf(
            "Specification-Title" to title,
            "Implementation-Title" to artifact,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to developerName,
            "Built-By" to developerId,
            "Build-Jdk" to System.getProperty("java.home"),
            "Created-By" to
                "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})"))
  }
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle jvm-test-suite Plugin <<< -----------------------
// ----------------------------------------------------------------------------

tasks.test {
  // Use JUnit Platform for unit tests.
  useJUnitPlatform()
  // WARNING: If a serviceability tool is in use, please run with
  // -XX:+EnableDynamicAgentLoading to hide this warning
  jvmArgs("-XX:+EnableDynamicAgentLoading")
  // report is always generated after tests run
  finalizedBy(tasks.jacocoTestReport)
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle maven-puglish Plugin <<< ------------------------
// ----------------------------------------------------------------------------

publishing {
  publications {
    val developerEmail: String by project

    val scmConnection: String by project
    val scmUrl: String by project

    val license: String by project
    val licenseUrl: String by project

    create<MavenPublication>("maven") {
      groupId = project.group.toString()
      artifactId = artifact
      version = project.version.toString()

      from(components["java"])

      pom {
        name = title
        description = project.description
        inceptionYear = "2024"
        packaging = "jar"

        licenses {
          license {
            name = license
            url = licenseUrl
          }
        }
        developers {
          developer {
            id = developerId
            name = developerName
            email = developerEmail
          }
        }
        scm {
          connection = scmConnection
          developerConnection = scmConnection
          url = scmUrl
        }
      }
    }
  }

  repositories {
    val repsyUrl: String by project
    val repsyUsername: String by project
    val repsyPassword: String by project

    maven {
      url = uri(repsyUrl)
      credentials {
        username = repsyUsername
        password = repsyPassword
      }
    }
  }
}

// ----------------------------------------------------------------------------
// --------------- >>> com.diffplug.spotless Plugin <<< -----------------------
// ----------------------------------------------------------------------------

spotless {
  kotlin {
    ktfmt()
    ktlint()
  }

  kotlinGradle {
    target("*.gradle.kts")
    ktfmt()
  }
}

// ----------------------------------------------------------------------------
// --------------- >>> net.researchgate.release Plugin <<< --------------------
// ----------------------------------------------------------------------------

release {
  with(git) {
    pushReleaseVersionBranch.set("release")
    requireBranch.set("main")
  }
}

// ----------------------------------------------------------------------------
// --------------- >>> org.jetbrains.kotlin.jvm Plugin <<< --------------------
// ----------------------------------------------------------------------------

kotlin {
  compilerOptions {
    /**
     * Java types used by Kotlin relaxes the null-safety checks. And the Spring Framework provides
     * null-safety annotations that could be potentially used by Kotlin types. Therefore, we need to
     * make jsr305 "strict" to ensure null-safety checks is NOT relaxed in Kotlin when Java
     * annotations, which are Kotlin platform types, are used.
     */
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks.compileKotlin { dependsOn("spotlessApply") }

// ----------------------------------------------------------------------------
// --------------- >>> org.sonarqube Plugin <<< -------------------------------
// ----------------------------------------------------------------------------

sonar {
  properties {
    // environment must have SONAR_TOKEN
    // export SONAR_TOKEN="SECRET"
    property("sonar.projectKey", "rubensgomes_helloworld-ms")
    property("sonar.organization", "rubensgomes")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}

tasks.sonar { dependsOn("test") }

// ----------------------------------------------------------------------------
// --------------- >>> org.springframework.boot Plugin <<< --------------------
// ----------------------------------------------------------------------------

tasks.bootRun {
  // The main function declared inside the package containing the file
  // "App.kt" is compiled into static methods of a Java class named AppKt
  mainClass.set("com.rubensgomes.helloworld.AppKt")
  jvmArgs(listOf<String>("Xmx256m"))
  dependsOn("check")
}
