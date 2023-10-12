import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.shadow)
}

// ------------------------------------------------------------------
// Attributes
// ------------------------------------------------------------------

group = "io.dotanuki"
version = evaluateVersion()

// ------------------------------------------------------------------
// Plugins
// ------------------------------------------------------------------

kotlin {
    jvmToolchain(11)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/detekt.yml")
}

configure<KtlintExtension> {
    filter {
        exclude { element -> element.file.path.contains("generated/") }
    }
}

// ------------------------------------------------------------------
// Dependencies
// ------------------------------------------------------------------

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ajalt.clikt)
    implementation(libs.ajalt.mordant)
    implementation(libs.arrow.core)

    // Test only
    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
}

// ------------------------------------------------------------------
// Tasks
// ------------------------------------------------------------------

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.set(
        listOf("-Xcontext-receivers")
    )
}

tasks.withType<ShadowJar>().configureEach {
    minimize()
    manifest {
        attributes["Add-Opens"] = "java.base/java.lang.invoke"
        attributes["Main-Class"] = "io.dotanuki.arw.MainKt"
        archiveFileName.set("arw-$version.jar")
    }
}

// ------------------------------------------------------------------
// Other helpers
// ------------------------------------------------------------------

fun evaluateVersion(): String =
    File("$rootDir/src/main/resources/versions.properties")
        .inputStream()
        .use { stream ->
            Properties().apply { load(stream) }["latest"].toString()
        }
