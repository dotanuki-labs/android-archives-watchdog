package io.dotanuki.aaw.features.overview

import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.serialization.json.Json

data class OverviewContext(
    val terminal: Terminal,
    val jsonSerializer: Json
)
