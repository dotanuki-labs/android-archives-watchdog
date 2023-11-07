/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.baseline

import com.github.ajalt.mordant.terminal.Terminal
import io.dotanuki.aaw.core.logging.Logger
import io.dotanuki.aaw.core.logging.LoggingContext
import net.peanuuutz.tomlkt.Toml

data class BaselineContext(
    val terminal: Terminal,
    val tomlSerializer: Toml,
    override val logger: Logger
) : LoggingContext
