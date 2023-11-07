/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.overview

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
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
import io.dotanuki.aaw.core.errors.ErrorAware
import io.dotanuki.aaw.core.filesystem.ValidatedFile
import io.dotanuki.aaw.core.logging.LoggingContext
import kotlin.system.exitProcess

context (OverviewContext, LoggingContext)
class OverviewCommand : CliktCommand(
    help = "aaw overview -a/--archive <path/to/archive> [--console | --json] ",
    name = "overview"
) {

    private val switches = listOf("--json" to "json", "--console" to "console").toTypedArray()

    private val format: String by option().switch(*switches).default("console")
    private val pathToArchive: String by option("-a", "--archive").required()

    private val reporter by lazy {
        OverviewReporter()
    }

    override fun run() {
        recover(::extractOverview, ::reportFailure)
    }

    context (ErrorAware)
    private fun extractOverview() {
        val analysed = AndroidArtifactAnalyser.analyse(ValidatedFile(pathToArchive))

        val overview = with(analysed) {
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
                totalProviders = componentCount(AndroidComponentType.PROVIDER)
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
