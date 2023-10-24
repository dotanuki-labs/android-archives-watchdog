
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import java.util.*

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.shadow)
    alias(libs.plugins.test.logger)
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
    google()
}

dependencies {
    implementation(libs.ajalt.clikt)
    implementation(libs.ajalt.mordant)
    implementation(libs.arrow.core)
    implementation(libs.android.tools.apkanalyzer)
    implementation(libs.android.tools.binaryresources)
    implementation(libs.android.tools.common)
    implementation(libs.android.tools.repository)
    implementation(libs.android.tools.sdkcommon)
    implementation(libs.android.tools.bundletool)
    implementation(libs.android.tools.sdklib)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.jvm)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.peanutz.tomlkt)

    // Test only
    testImplementation(libs.junit)
    testImplementation(libs.google.guava)
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
