package io.dotanuki.aaw

import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.mordant.terminal.Terminal
import io.dotanuki.aaw.core.cli.AawEntrypoint
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
object Injection {

    private val terminal by lazy {
        Terminal()
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
        OverviewContext(terminal, jsonSerializer)
    }

    private val overviewCommand by lazy {
        with(overviewContext) {
            OverviewCommand()
        }
    }

    private val baselineContext by lazy {
        BaselineContext(terminal, tomlSerializer)
    }

    private val generateCommand by lazy {
        with(baselineContext) {
            GenerateCommand()
        }
    }

    private val compareContext by lazy {
        CompareContext(terminal, tomlSerializer, jsonSerializer)
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
}
