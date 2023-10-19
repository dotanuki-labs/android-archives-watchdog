package io.dotanuki.arw.features.overview

import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.rendering.TextColors.magenta
import com.github.ajalt.mordant.table.table
import io.dotanuki.arw.core.infrastructure.cli.emptyLine

object OverviewReporter {

    context (OverviewContext)
    fun reportSuccess(overview: ArtifactOverview, format: String) {
        when (format) {
            "console" -> reportAsText(overview)
            "json" -> reportAsJson(overview)
        }
    }

    context (OverviewContext)
    private fun reportAsJson(overview: ArtifactOverview) {
        val content = jsonSerializer.encodeToString(ArtifactOverview.serializer(), overview)
        terminal.println(content)
    }

    context (OverviewContext)
    private fun reportAsText(overview: ArtifactOverview) {
        val content = with(overview) {
            table {
                header { row(cyan("Attribute"), cyan("Evaluation")) }
                body { row(magenta("Application Id"), overview.applicationId) }
                body { row(magenta("Debuggable"), overview.debuggable.asAffirmation()) }
                body { row(magenta("Minimum SDK"), minSdk) }
                body { row(magenta("Target SDK"), targetSdk) }
                body { row(magenta("Total Used Features"), totalUsedFeatures) }
                body { row(magenta("Total Manifest permissions"), totalPermissions) }
                body { row(magenta("Dangerous permissions"), dangerousPermissions.asAffirmation()) }
                body { row(magenta("Activities"), totalActivities) }
                body { row(magenta("Services"), totalServices) }
                body { row(magenta("Broadcast Receivers"), totalReceivers) }
                body { row(magenta("Content Providers"), totalProviders) }
            }
        }
        terminal.emptyLine()
        terminal.println(content)
        terminal.emptyLine()
    }

    private fun Boolean.asAffirmation() = if (this) "Yes" else "No"
}
