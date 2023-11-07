/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.overview

import com.github.ajalt.mordant.terminal.Terminal
import io.dotanuki.aaw.core.logging.Logger
import io.dotanuki.aaw.core.logging.LoggingContext
import kotlinx.serialization.json.Json

data class OverviewContext(
    val terminal: Terminal,
    val jsonSerializer: Json,
    override val logger: Logger
) : LoggingContext
