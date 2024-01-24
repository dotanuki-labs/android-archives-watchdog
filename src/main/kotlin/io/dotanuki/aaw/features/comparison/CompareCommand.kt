/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.comparison

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.switch
import io.dotanuki.aaw.core.cli.ExitCodes
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.errors.ErrorAware
import io.dotanuki.aaw.core.filesystem.ValidatedFile
import io.dotanuki.aaw.core.logging.Logging
import io.dotanuki.aaw.core.toml.ValidatedTOML
import kotlin.system.exitProcess

context (CompareContext, Logging)
class CompareCommand : CliktCommand(
    help = "aaw compare -a/--archive <path/to/archive> -b/--baseline <path/to/baseline>",
    name = "compare",
) {
    private val outputFormats = listOf("--json" to "json", "--console" to "console").toTypedArray()

    private val pathToArchive: String by option("-a", "--archive").required()
    private val pathToBaseline: String by option("-b", "--baseline").required()
    private val exitWithFailure by option("--fail").flag(default = false)
    private val format: String by option().switch(*outputFormats).default("console")

    private val reporter by lazy {
        ComparisonReporter()
    }

    override fun run() {
        recover(::performComparison, ::reportFailure)
    }

    context (ErrorAware)
    private fun performComparison() {
        val current = analyser.analyse(ValidatedFile(pathToArchive))
        val reference = ValidatedTOML(ValidatedFile(pathToBaseline))
        val comparison = comparator.compare(current, reference.asBaseline())
        reporter.reportChanges(comparison, format)

        if (exitWithFailure && comparison.isNotEmpty()) {
            exitProcess(ExitCodes.FAILURE)
        }
    }

    private fun reportFailure(surfaced: AawError) {
        logger.error(surfaced)
        exitProcess(ExitCodes.FAILURE)
    }
}
