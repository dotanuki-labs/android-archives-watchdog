package io.dotanuki.arw.features.overview

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.switch
import io.dotanuki.arw.core.android.AnalysedArtifact
import io.dotanuki.arw.core.android.AndroidArtifactAnalyser
import io.dotanuki.arw.core.android.AndroidComponentType
import io.dotanuki.arw.core.android.AndroidPermissions
import io.dotanuki.arw.core.cli.ErrorReporter
import io.dotanuki.arw.core.errors.ErrorAware
import io.dotanuki.arw.core.filesystem.ValidatedFile

context (OverviewContext)
class OverviewCommand : CliktCommand(
    help = "arw overview -a/--archive <path/to/archive> [--console | --json] ",
    name = "overview"
) {

    private val switches = listOf("--json" to "json", "--console" to "console").toTypedArray()

    private val format: String by option().switch(*switches).default("console")
    private val pathToArchive: String by option("-a", "--archive").required()
    private val debugMode by option("--stacktrace").flag(default = false)

    override fun run() {
        ErrorReporter.printStackTraces = debugMode
        recover(::extractOverview, ErrorReporter::reportFailure)
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

        OverviewReporter.reportSuccess(overview, format)
    }

    private fun AnalysedArtifact.componentCount(selected: AndroidComponentType) =
        androidComponents.count { it.type == selected }
}
