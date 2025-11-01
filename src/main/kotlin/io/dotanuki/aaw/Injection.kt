/*
 * Copyright 2025 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw

import com.github.ajalt.clikt.core.subcommands
import io.dotanuki.aaw.core.android.AndroidArtifactAnalyser
import io.dotanuki.aaw.core.android.AndroidSDKBridge
import io.dotanuki.aaw.core.cli.AawEntrypoint
import io.dotanuki.aaw.core.logging.Logging
import io.dotanuki.aaw.features.baseline.BaselineWriter
import io.dotanuki.aaw.features.baseline.GenerateCommand
import io.dotanuki.aaw.features.comparison.ArtifactsComparator
import io.dotanuki.aaw.features.comparison.CompareCommand
import io.dotanuki.aaw.features.comparison.ComparisonReporter
import io.dotanuki.aaw.features.overview.OverviewCommand
import io.dotanuki.aaw.features.overview.OverviewReporter
import io.dotanuki.aaw.features.version.AppVersionFinder
import io.dotanuki.aaw.features.version.VersionCommand
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import net.peanuuutz.tomlkt.Toml

@OptIn(ExperimentalSerializationApi::class)
class Injection(
    private val verboseMode: Boolean,
) {
    private val logger by lazy {
        Logging.create(verboseMode).logger
    }

    private val jsonSerializer by lazy {
        Json {
            namingStrategy = JsonNamingStrategy.SnakeCase
        }
    }

    private val tomlSerializer by lazy {
        Toml {
            ignoreUnknownKeys = true
        }
    }

    private val artifactAnalyser by lazy {
        AndroidArtifactAnalyser(logger, AndroidSDKBridge())
    }

    private val overviewCommand by lazy {
        val reporter = OverviewReporter(logger, jsonSerializer)
        OverviewCommand(logger, artifactAnalyser, reporter)
    }

    private val generateCommand by lazy {
        val baselineWriter = BaselineWriter(logger, tomlSerializer)
        GenerateCommand(logger, artifactAnalyser, baselineWriter)
    }

    private val artifactsComparator by lazy {
        ArtifactsComparator(logger)
    }

    private val compareCommand by lazy {
        val reporter = ComparisonReporter(logger, jsonSerializer)
        CompareCommand(logger, tomlSerializer, artifactAnalyser, artifactsComparator, reporter)
    }

    private val versionCommand by lazy {
        val versionFinder = AppVersionFinder(logger)
        VersionCommand(logger, versionFinder)
    }

    val entrypoint by lazy {
        AawEntrypoint().subcommands(
            overviewCommand,
            generateCommand,
            compareCommand,
            versionCommand,
        )
    }
}
