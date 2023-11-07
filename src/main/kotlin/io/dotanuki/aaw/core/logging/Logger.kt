/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.logging

import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.terminal.Terminal

data class Logger(val terminal: Terminal) {
    fun newLine() {
        terminal.println()
    }

    fun log(message: String) {
        terminal.println(message)
    }

    fun info(message: String) {
        terminal.println(cyan(message))
    }

    fun error(message: String) {
        terminal.println(red(message))
    }
}
