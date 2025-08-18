/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.comparison

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.switch
import io.dotanuki.aaw.core.android.AndroidArtifactAnalyser
import io.dotanuki.aaw.core.cli.ExitCodes
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.filesystem.ValidatedFile
import io.dotanuki.aaw.core.logging.Logger
import io.dotanuki.aaw.core.toml.WatchdogConfig
import net.peanuuutz.tomlkt.Toml
import java.io.File
import kotlin.system.exitProcess

class CompareCommand(
    private val logger: Logger,
    private val tomlSerializer: Toml,
    private val artifactAnalyser: AndroidArtifactAnalyser,
    private val artifactsComparator: ArtifactsComparator,
    private val comparisonReporter: ComparisonReporter,
) : CliktCommand(
        name = "compare",
    ) {
    private val outputFormats = listOf("--json" to "json", "--console" to "console").toTypedArray()

    private val pathToArchive: String by option("-a", "--archive").required()
    private val pathToBaseline: String by option("-b", "--baseline").required()
    private val exitWithFailure by option("--fail").flag(default = false)
    private val format: String by option().switch(*outputFormats).default("console")

    override fun help(context: Context): String =
        "aaw compare -a/--archive <path/to/archive> -b/--baseline <path/to/baseline>"

    override fun run() {
        ValidatedFile(pathToArchive)
            .flatMap { artifactPath ->
                ValidatedFile(pathToBaseline).flatMap { baselinePath ->
                    Pair(artifactPath, baselinePath).right()
                }
            }.flatMap { (artifactPath, baselinePath) -> performComparison(artifactPath, baselinePath) }
            .onLeft { reportFailure(it) }
            .onRight { comparison ->
                comparisonReporter.reportChanges(comparison, format)
                if (exitWithFailure && comparison.isNotEmpty()) {
                    exitProcess(ExitCodes.FAILURE)
                }
            }
    }

    private fun performComparison(
        artifactPath: String,
        baselinePath: String,
    ): Either<AawError, Set<ComparisonFinding>> {
        val current = artifactAnalyser.analyse(artifactPath)

        val reference =
            Either
                .catch {
                    tomlSerializer.decodeFromString(WatchdogConfig.serializer(), File(baselinePath).readText())
                }.mapLeft {
                    AawError("Failed when parsing configuration", it)
                }

        return current.flatMap { analysedArtifact ->
            reference.flatMap { watchdogConfig ->
                artifactsComparator.compare(analysedArtifact, watchdogConfig.asBaseline())
            }
        }
    }

    private fun reportFailure(surfaced: AawError) {
        logger.error(surfaced)
        exitProcess(ExitCodes.FAILURE)
    }
}
