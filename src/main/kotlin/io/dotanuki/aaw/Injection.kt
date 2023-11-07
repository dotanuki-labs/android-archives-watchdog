/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw

import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.mordant.terminal.Terminal
import io.dotanuki.aaw.core.cli.AawEntrypoint
import io.dotanuki.aaw.core.logging.Logger
import io.dotanuki.aaw.features.baseline.BaselineContext
import io.dotanuki.aaw.features.baseline.GenerateCommand
import io.dotanuki.aaw.features.comparison.CompareCommand
import io.dotanuki.aaw.features.comparison.CompareContext
import io.dotanuki.aaw.features.overview.OverviewCommand
import io.dotanuki.aaw.features.overview.OverviewContext
import io.dotanuki.aaw.features.version.VersionCommand
import io.dotanuki.aaw.features.version.VersionContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import net.peanuuutz.tomlkt.Toml

@OptIn(ExperimentalSerializationApi::class)
class Injection private constructor(
    private val verboseMode: Boolean
) {

    private val terminal by lazy {
        Terminal()
    }

    private val logger by lazy {
        Logger(terminal, verboseMode)
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

    private val overviewContext by lazy {
        OverviewContext(terminal, jsonSerializer, logger)
    }

    private val overviewCommand by lazy {
        with(overviewContext) {
            OverviewCommand()
        }
    }

    private val baselineContext by lazy {
        BaselineContext(terminal, tomlSerializer, logger)
    }

    private val generateCommand by lazy {
        with(baselineContext) {
            GenerateCommand()
        }
    }

    private val compareContext by lazy {
        CompareContext(terminal, tomlSerializer, jsonSerializer, logger)
    }

    private val compareCommand by lazy {
        with(compareContext) {
            CompareCommand()
        }
    }

    private val versionContext by lazy {
        VersionContext(terminal)
    }

    private val versionCommand by lazy {
        with(versionContext) {
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

    companion object {
        fun inject(args: Array<String>) = Injection(false)
    }
}
