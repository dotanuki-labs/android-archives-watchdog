/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.comparison

import com.github.ajalt.mordant.terminal.Terminal
import io.dotanuki.aaw.core.logging.Logger
import io.dotanuki.aaw.core.logging.LoggingContext
import kotlinx.serialization.json.Json
import net.peanuuutz.tomlkt.Toml

data class CompareContext(
    val terminal: Terminal,
    val tomlSerializer: Toml,
    val jsonSerializer: Json,
    override val logger: Logger
) : LoggingContext
