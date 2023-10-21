package io.dotanuki.arw

import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.mordant.terminal.Terminal
import io.dotanuki.arw.core.infrastructure.cli.ARWCommand
import io.dotanuki.arw.features.baseline.BaselineContext
import io.dotanuki.arw.features.baseline.GenerateCommand
import io.dotanuki.arw.features.comparison.CompareCommand
import io.dotanuki.arw.features.comparison.CompareContext
import io.dotanuki.arw.features.overview.OverviewCommand
import io.dotanuki.arw.features.overview.OverviewContext
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
        CompareContext(terminal, tomlSerializer)
    }

    private val compareCommand by lazy {
        with(compareContext) {
            CompareCommand()
        }
    }

    val entrypoint by lazy {
        ARWCommand().subcommands(overviewCommand, generateCommand, compareCommand)
    }
}
