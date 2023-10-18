package io.dotanuki.arw.features.overview

import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.rendering.TextColors.magenta
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import io.dotanuki.arw.core.domain.errors.ArwError
import io.dotanuki.arw.core.infrastructure.cli.emptyLine
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

@OptIn(ExperimentalSerializationApi::class)
object OverviewReporter {

    private val terminal by lazy { Terminal() }
    private val jsonEncoder by lazy {
        Json {
            namingStrategy = JsonNamingStrategy.SnakeCase
        }
    }

    fun reportFailure(error: ArwError) {
        terminal.emptyLine()
        terminal.println(red(error.description))
        terminal.emptyLine()
    }

    fun reportSuccess(overview: ArtifactOverview, format: String) {
        when (format) {
            "console" -> reportAsText(overview)
            "json" -> reportAsJson(overview)
        }
    }

    private fun reportAsJson(overview: ArtifactOverview) {
        val content = jsonEncoder.encodeToString(ArtifactOverview.serializer(), overview)
        terminal.println(content)
    }

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
            }
        }
        terminal.emptyLine()
        terminal.println(content)
        terminal.emptyLine()
    }

    private fun Boolean.asAffirmation() = if (this) "Yes" else "No"
}
