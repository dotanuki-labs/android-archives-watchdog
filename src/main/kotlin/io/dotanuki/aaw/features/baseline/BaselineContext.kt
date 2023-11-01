/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.baseline

import com.github.ajalt.mordant.terminal.Terminal
import net.peanuuutz.tomlkt.Toml

data class BaselineContext(
    val terminal: Terminal,
    val tomlSerializer: Toml
)
