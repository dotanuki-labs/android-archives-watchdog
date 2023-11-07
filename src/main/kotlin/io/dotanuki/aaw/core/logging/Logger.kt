/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.logging

import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.rendering.Widget
import com.github.ajalt.mordant.terminal.Terminal

data class Logger(
    private val terminal: Terminal,
    private val verboseMode: Boolean
) {
    fun newLine() {
        terminal.println()
    }

    fun info(widget: Widget) {
        terminal.println(widget)
    }

    fun info(message: String) {
        terminal.println(message)
    }

    fun debug(message: String) {
        if (verboseMode) {
            terminal.println(message)
        }
    }

    fun error(message: String) {
        terminal.println(red(message))
    }
}
