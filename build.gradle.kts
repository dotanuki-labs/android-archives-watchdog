import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.jvm)
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
