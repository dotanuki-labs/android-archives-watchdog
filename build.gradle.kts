import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

// ------------------------------------------------------------------
// Attributes
// ------------------------------------------------------------------

group = "io.dotanuki"
version = "0.1.0"

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
