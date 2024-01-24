/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.cli

import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.logging.Logging
import kotlin.system.exitProcess

object ErrorReporter {
    var printStackTraces: Boolean = false

    context (Logging)
    fun reportFailure(surfaced: AawError) {
        logger.newLine()
        logger.error(surfaced.description)

        if (printStackTraces) {
            val trace = surfaced.wrapped ?: return
            logger.newLine()
            trace.printStackTrace()
        }

        logger.newLine()
        exitProcess(ExitCodes.FAILURE)
    }
}
