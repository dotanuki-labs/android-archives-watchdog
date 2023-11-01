/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.version

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import io.dotanuki.aaw.core.cli.ErrorReporter
import io.dotanuki.aaw.core.errors.ErrorAware

context (VersionContext)
class VersionCommand : CliktCommand(
    help = "aaw version",
    name = "version"
) {

    private val debugMode by option("--stacktrace").flag(default = false)

    override fun run() {
        ErrorReporter.printStackTraces = debugMode
        recover(::printVersion, ErrorReporter::reportFailure)
    }

    context (ErrorAware)
    private fun printVersion() {
        val appVersion = AppVersionFinder.find()
        terminal.println("aaw - v${appVersion.current}")
    }
}
