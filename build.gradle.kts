/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import java.util.Properties

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
    jvmToolchain(17)
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
        listOf("-Xcontext-receivers"),
    )
}

tasks.withType<ShadowJar>().configureEach {
    manifest {
        attributes["Add-Opens"] = "java.base/java.lang.invoke"
        attributes["Main-Class"] = "io.dotanuki.aaw.MainKt"
        archiveFileName.set("aaw-$version.jar")
    }
}

val assembleExecutable by tasks.registering(DefaultTask::class) {
    description = "Creates an executable standalone binary for this project"
    group = "Build"

    dependsOn(tasks.shadowJar)

    val aawShadowJarFile =
        tasks.shadowJar.orNull
            ?.outputs
            ?.files
            ?.singleFile
            ?: throw GradleException("Missing aaw shadowJar file")

    inputs.files(aawShadowJarFile)

    val outputDirectoryPath = "${layout.buildDirectory.get()}/bin"
    val executableOutputPath = "$outputDirectoryPath/aaw"
    outputs.files(executableOutputPath)

    doLast {
        File(executableOutputPath).apply {
            logger.lifecycle("Creating the self-executable file: $outputDirectoryPath")
            writeText(
                """
                #! /usr/bin/env bash

                exec java -Xmx1024m -jar "$0" "$@"

                """.trimIndent(),
            )
            appendBytes(aawShadowJarFile.readBytes())

            setExecutable(true, false)
        }
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
