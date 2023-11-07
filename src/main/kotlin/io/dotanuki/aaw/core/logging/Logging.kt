/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.logging

import com.github.ajalt.mordant.terminal.Terminal

interface Logging {

    val logger: Logger

    companion object {
        fun create(terminal: Terminal) = object : Logging {
            override val logger = Logger(terminal)
        }
    }
}
