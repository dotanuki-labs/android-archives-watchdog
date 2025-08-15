/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.baseline

import arrow.core.flatMap
import arrow.core.right
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.dotanuki.aaw.core.android.AnalysedArtifact
import io.dotanuki.aaw.core.cli.ExitCodes
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.filesystem.ValidatedFile
import io.dotanuki.aaw.core.logging.Logging
import io.dotanuki.aaw.core.toml.WatchdogConfig
import kotlin.system.exitProcess

context (BaselineContext, Logging)
class GenerateCommand :
    CliktCommand(
        name = "generate",
    ) {
    private val pathToArchive: String by option("-a", "--archive").required()
    private val trustedPackages: String? by option("-t", "--trust")

    private val writer by lazy {
        BaselineWriter()
    }

    override fun help(context: Context): String =
        "aaw generate -a/--archive <path/to/archive> -t/--trust <package1,package2,...>"

    override fun run() {
        ValidatedFile(pathToArchive)
            .flatMap {
                analyser
                    .analyse(it)
                    .flatMap { analysed ->
                        ValidatedPackages(trustedPackages)
                            .flatMap { validatedPackages ->
                                Pair(analysed, validatedPackages).right()
                            }
                    }
            }.onLeft { reportFailure(it) }
            .onRight { extractBaseline(it.first, it.second) }
    }

    private fun extractBaseline(
        analysed: AnalysedArtifact,
        packagesToIgnore: List<String>,
    ) {
        val baseline = WatchdogConfig.from(analysed, packagesToIgnore)
        val outputFile = "${analysed.applicationId}.toml"
        writer.write(baseline, outputFile)
    }

    private fun reportFailure(surfaced: AawError) {
        logger.error(surfaced)
        exitProcess(ExitCodes.FAILURE)
    }
}
