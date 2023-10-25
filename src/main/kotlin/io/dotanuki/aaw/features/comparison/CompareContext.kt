package io.dotanuki.aaw.features.comparison

import com.github.ajalt.mordant.terminal.Terminal
import net.peanuuutz.tomlkt.Toml

data class CompareContext(
    val terminal: Terminal,
    val tomlSerializer: Toml
)
