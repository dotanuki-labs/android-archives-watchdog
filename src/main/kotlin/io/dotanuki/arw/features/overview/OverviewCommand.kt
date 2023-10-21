package io.dotanuki.arw.features.overview

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.switch
import io.dotanuki.arw.core.domain.errors.ErrorAware
import io.dotanuki.arw.core.domain.models.AnalysedArtifact
import io.dotanuki.arw.core.domain.models.AndroidComponentType
import io.dotanuki.arw.core.domain.models.AndroidPermissions
import io.dotanuki.arw.core.infrastructure.android.AndroidArtifactAnalyser
import io.dotanuki.arw.core.infrastructure.cli.ErrorReporter

context (OverviewContext)
class OverviewCommand : CliktCommand(
    help = "arw overview [--console | --json] ",
    name = "overview"
) {

    private val switches = listOf("--json" to "json", "--console" to "console").toTypedArray()

    private val format: String by option().switch(*switches).default("console")
    private val target: String by option("-t", "--target").required()

    override fun run() {
        recover(::extractOverview, ErrorReporter::reportFailure)
    }

    context (ErrorAware)
    private fun extractOverview() {
        val analysed = AndroidArtifactAnalyser.analyse(target)

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

        OverviewReporter.reportSuccess(overview, format)
    }

    private fun AnalysedArtifact.componentCount(selected: AndroidComponentType) =
        androidComponents.count { it.type == selected }
}
