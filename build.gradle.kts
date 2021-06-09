import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin ("jvm") version "1.5.10"
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "ca.dealo"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.1.0"
val junitJupiterVersion = "5.7.0"

val mainVerticleName = "ca.dealo.deals_web_api.DealsVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-auth-jwt")
  implementation("io.vertx:vertx-health-check")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-web-openapi")
  implementation("io.vertx:vertx-micrometer-metrics")
  implementation("io.vertx:vertx-json-schema")
  implementation("io.vertx:vertx-shell")
  implementation("io.vertx:vertx-web-api-contract")
  implementation("io.vertx:vertx-auth-oauth2")
  implementation("io.vertx:vertx-config")
  implementation("io.vertx:vertx-web-graphql")
  implementation("io.vertx:vertx-pg-client")
  implementation("io.vertx:vertx-opentelemetry")
  implementation("io.vertx:vertx-circuit-breaker")
  implementation("io.vertx:vertx-lang-kotlin-coroutines")
  implementation("io.vertx:vertx-kafka-client")
  implementation("io.vertx:vertx-mongo-client")
  implementation("io.vertx:vertx-lang-kotlin")
  implementation(kotlin("stdlib-jdk8"))
  testImplementation("io.vertx:vertx-unit")
  testImplementation("junit:junit:4.13.1")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnit()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
