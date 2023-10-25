package io.dotanuki.aaw.features.baseline

import com.github.ajalt.mordant.terminal.Terminal
import net.peanuuutz.tomlkt.Toml

data class BaselineContext(
    val terminal: Terminal,
    val tomlSerializer: Toml
)
