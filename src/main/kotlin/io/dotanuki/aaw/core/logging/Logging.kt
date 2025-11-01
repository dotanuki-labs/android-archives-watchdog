/*
 * Copyright 2025 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.logging

import com.github.ajalt.mordant.terminal.Terminal

data class Logging(
    val logger: Logger,
) {
    companion object {
        fun create(verboseMode: Boolean = false): Logging {
            val terminal = Terminal()
            val logger = Logger(terminal, verboseMode)
            return Logging(logger)
        }
    }
}
