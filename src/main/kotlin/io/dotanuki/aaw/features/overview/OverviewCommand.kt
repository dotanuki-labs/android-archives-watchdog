/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.overview

import arrow.core.flatMap
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.switch
import io.dotanuki.aaw.core.android.AnalysedArtifact
import io.dotanuki.aaw.core.android.AndroidArtifactAnalyser
import io.dotanuki.aaw.core.android.AndroidComponentType
import io.dotanuki.aaw.core.android.AndroidPermissions
import io.dotanuki.aaw.core.cli.ExitCodes
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.filesystem.ValidatedFile
import io.dotanuki.aaw.core.logging.Logger
import kotlin.system.exitProcess

class OverviewCommand(
    private val logger: Logger,
    private val artifactAnalyser: AndroidArtifactAnalyser,
    private val reporter: OverviewReporter,
) : CliktCommand(
        name = "overview",
    ) {
    private val switches = listOf("--json" to "json", "--console" to "console").toTypedArray()

    private val format: String by option().switch(*switches).default("console")
    private val pathToArchive: String by option("-a", "--archive").required()

    override fun help(context: Context): String = "aaw overview -a/--archive <path/to/archive> [--console | --json]"

    override fun run() {
        ValidatedFile(pathToArchive)
            .flatMap { artifactAnalyser.analyse(it) }
            .onLeft { reportFailure(it) }
            .onRight { extractOverview(it) }
    }

    private fun extractOverview(analysed: AnalysedArtifact) {
        val overview =
            with(analysed) {
                ArtifactOverview(
                    applicationId,
                    minSdk,
                    targetSdk,
                    totalUsedFeatures = androidFeatures.size,
                    totalPermissions = androidPermissions.size,
                    dangerousPermissions = AndroidPermissions.hasDangerous(androidPermissions),
                    totalActivities = componentCount(AndroidComponentType.ACTIVITY),
                    totalServices = componentCount(AndroidComponentType.SERVICE),
                    totalReceivers = componentCount(AndroidComponentType.RECEIVER),
                    totalProviders = componentCount(AndroidComponentType.PROVIDER),
                )
            }

        reporter.reportSuccess(overview, format)
    }

    private fun reportFailure(surfaced: AawError) {
        logger.error(surfaced)
        exitProcess(ExitCodes.FAILURE)
    }

    private fun AnalysedArtifact.componentCount(selected: AndroidComponentType) =
        androidComponents.count { it.type == selected }
}
