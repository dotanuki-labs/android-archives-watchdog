/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw

import com.github.ajalt.clikt.core.subcommands
import io.dotanuki.aaw.core.android.AndroidArtifactAnalyser
import io.dotanuki.aaw.core.cli.AawEntrypoint
import io.dotanuki.aaw.core.logging.Logging
import io.dotanuki.aaw.features.baseline.BaselineContext
import io.dotanuki.aaw.features.baseline.GenerateCommand
import io.dotanuki.aaw.features.comparison.ArtifactsComparator
import io.dotanuki.aaw.features.comparison.CompareCommand
import io.dotanuki.aaw.features.comparison.CompareContext
import io.dotanuki.aaw.features.overview.OverviewCommand
import io.dotanuki.aaw.features.overview.OverviewContext
import io.dotanuki.aaw.features.version.VersionCommand
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import net.peanuuutz.tomlkt.Toml

@OptIn(ExperimentalSerializationApi::class)
class Injection(
    private val verboseMode: Boolean
) {

    private val loggingContext by lazy {
        Logging.create(verboseMode)
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
        with(loggingContext) {
            AndroidArtifactAnalyser()
        }
    }

    private val overviewContext by lazy {
        OverviewContext(jsonSerializer, artifactAnalyser)
    }

    private val overviewCommand by lazy {
        with(loggingContext) {
            with(overviewContext) {
                OverviewCommand()
            }
        }
    }

    private val baselineContext by lazy {
        BaselineContext(tomlSerializer, artifactAnalyser)
    }

    private val generateCommand by lazy {
        with(loggingContext) {
            with(baselineContext) {
                GenerateCommand()
            }
        }
    }

    private val comparator by lazy {
        with(loggingContext) {
            ArtifactsComparator()
        }
    }

    private val compareContext by lazy {
        CompareContext(tomlSerializer, jsonSerializer, artifactAnalyser, comparator)
    }

    private val compareCommand by lazy {
        with(loggingContext) {
            with(compareContext) {
                CompareCommand()
            }
        }
    }

    private val versionCommand by lazy {
        with(loggingContext) {
            VersionCommand()
        }
    }

    val entrypoint by lazy {
        AawEntrypoint().subcommands(
            overviewCommand,
            generateCommand,
            compareCommand,
            versionCommand
        )
    }
}
