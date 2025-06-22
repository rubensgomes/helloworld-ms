plugins {
  id("distribution")
  id("idea")
  id("jacoco")
  id("java")
  `jvm-test-suite`
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

configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }

// --------------- >>> repositories <<< ---------------------------------------

repositories { mavenCentral() }

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
// https://docs.gradle.org/current/userguide/base_plugin.html

// run sonar independently since it requires a remote connection to sonarcloud.io
// tasks.check { dependsOn("sonar") }

// ----------------------------------------------------------------------------
// --------------- >>> Gradle Distribution Plugin <<< -------------------------
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/distribution_plugin.html

distributions { main { distributionBaseName = "helloworld-ms" } }

// ----------------------------------------------------------------------------
// --------------- >>> Gradle IDEA Plugin <<< ---------------------------------
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/idea_plugin.html

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle jaCoCo Plugin <<< -------------------------------
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/jacoco_plugin.html

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
// --------------- >>> Gradle Java Plugin <<< ---------------------------------
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/java_plugin.html

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
            "Specification-Title" to project.properties["title"],
            "Implementation-Title" to project.properties["artifact"],
            "Implementation-Version" to project.properties["version"],
            "Implementation-Vendor" to project.properties["developerName"],
            "Built-By" to project.properties["developerId"],
            "Build-Jdk" to System.getProperty("java.home"),
            "Created-By" to
                "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})"))
  }
}

// ----------------------------------------------------------------------------
// --------------- >>> Gradle JVM Test Suite Plugin <<< -----------------------
// ----------------------------------------------------------------------------
// https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html

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
// --------------- >>> com.diffplug.spotless Plugin <<< -----------------------
// ----------------------------------------------------------------------------
// https://github.com/diffplug/spotless

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
// https://github.com/researchgate/gradle-release

release {
  release {
    with(git) {
      pushReleaseVersionBranch.set("release")
      requireBranch.set("main")
    }
  }
}

// tasks.afterReleaseBuild { dependsOn("publish") }

// ----------------------------------------------------------------------------
// --------------- >>> org.jetbrains.kotlin.jvm Plugin <<< --------------------
// ----------------------------------------------------------------------------
// https://kotlinlang.org/docs/gradle-configure-project.html#kotlin-and-java-sources

kotlin {
  /**
   * Java types used by Kotlin relaxes the null-safety checks. And the Spring Framework provides
   * null-safety annotations that could be potentially used by Kotlin types. Therefore, we need to
   * make jsr305 "strict" to ensure null-safety checks is NOT relaxed in Kotlin when Java
   * annotations, which are Kotlin platform types, are used.
   */
  compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") }
}

tasks.compileKotlin { dependsOn("spotlessApply") }

// ----------------------------------------------------------------------------
// --------------- >>> org.sonarqube Plugin <<< -------------------------------
// ----------------------------------------------------------------------------
// https://docs.sonarsource.com/sonarqube-server/latest/analyzing-source-code/scanners/sonarscanner-for-gradle/

//  gradle.properties:
val sonarProjKey = project.findProperty("sonar.projectKey") as String
val sonarOrganization = project.findProperty("sonar.organization") as String
val sonarHostUrl = project.findProperty("sonar.host.url") as String

sonar {
  properties {
    // SONAR_TOKEN must be defined as an environment variable
    property("sonar.projectKey", sonarProjKey)
    property("sonar.organization", sonarOrganization)
    property("sonar.host.url", sonarHostUrl)
  }
}

// task.check includes jacocoTestReport
// tasks.sonar { dependsOn("jacocoTestReport") }
tasks.sonar { dependsOn("check") }

// ----------------------------------------------------------------------------
// --------------- >>> org.springframework.boot Plugin <<< --------------------
// ----------------------------------------------------------------------------
// https://docs.spring.io/spring-boot/gradle-plugin/index.html

springBoot {
  // The main function declared inside the package containing the file
  // "App.kt" is compiled into static methods of a Java class named AppKt
  mainClass.set("com.rubensgomes.helloworld.AppKt")
}

tasks.bootBuildImage {
  builder.set("paketobuildpacks/builder-jammy-base:latest")
  applicationDirectory.set("/spring")
  cleanCache.set(true)
  createdDate.set("now")
  val repository: String = project.findProperty("docker.image.repository").toString()
  val version: String = project.findProperty("version").toString()
  imageName.set("$repository:$version")
  imagePlatform.set("linux/amd64")
  network.set("bridge")
  verboseLogging.set(true)
  publish.set(true)
  docker {
    val token: String = System.getenv("DOCKERIO_PAT")
    builderRegistry {
      username.set("rubensgomes")
      password.set(token)
      email.set("rubens.s.gomes@gmail.com")
    }
    publishRegistry {
      username.set("rubensgomes")
      password.set(token)
      email.set("rubens.s.gomes@gmail.com")
    }
  }
}

tasks.bootJar {
  layered.enabled.set(true)
  dependsOn("check")
  manifest { attributes("Start-Class" to "com.rubensgomes.helloworld.AppKt") }
}

tasks.bootRun { dependsOn("check") }
