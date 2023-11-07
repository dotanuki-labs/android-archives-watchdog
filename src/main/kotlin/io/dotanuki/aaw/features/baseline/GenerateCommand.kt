/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.baseline

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.dotanuki.aaw.core.cli.ExitCodes
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.errors.ErrorAware
import io.dotanuki.aaw.core.filesystem.ValidatedFile
import io.dotanuki.aaw.core.logging.Logging
import io.dotanuki.aaw.core.toml.WatchdogConfig
import kotlin.system.exitProcess

context (BaselineContext, Logging)
class GenerateCommand : CliktCommand(
    help = "aaw generate -a/--archive <path/to/archive> -t/--trust <package1,package2,...>",
    name = "generate"
) {

    private val pathToArchive: String by option("-a", "--archive").required()
    private val trustedPackages: String? by option("-t", "--trust")

    private val writer by lazy {
        BaselineWriter()
    }

    override fun run() {
        recover(::extractBaseline, ::reportFailure)
    }

    context (ErrorAware)
    private fun extractBaseline() {
        val analysed = analyser.analyse(ValidatedFile(pathToArchive))
        val baseline = WatchdogConfig.from(analysed, ValidatedPackages(trustedPackages))
        val outputFile = "${analysed.applicationId}.toml"
        writer.write(baseline, outputFile)
    }

    private fun reportFailure(surfaced: AawError) {
        logger.error(surfaced)
        exitProcess(ExitCodes.FAILURE)
    }
}
