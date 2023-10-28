package io.dotanuki.aaw.features.comparison

import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.serialization.json.Json
import net.peanuuutz.tomlkt.Toml

data class CompareContext(
    val terminal: Terminal,
    val tomlSerializer: Toml,
    val jsonSerializer: Json
)
