package io.dotanuki.arw.features.overview

import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.rendering.TextColors.magenta
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import io.dotanuki.arw.core.domain.errors.ArwError
import io.dotanuki.arw.core.infrastructure.cli.emptyLine

object OverviewReporter {

    private val terminal by lazy { Terminal() }

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
        val content = with(overview) {
            jsonTemplate
                .replace(PLACEHOLDER_APP_ID, applicationId)
                .replace(PLACEHOLDER_MIN_SDK, minSdk.toString())
                .replace(PLACEHOLDER_TARGET_SDK, targetSdk.toString())
                .replace(PLACEHOLDER_TOTAL_PERMISSIONS, totalPermissions.toString())
                .replace(PLACEHOLDER_SENSITIVE_PERMISSIONS, dangerousPermissions.toString())
                .replace(PLACEHOLDER_DEBUGGABLE, debuggable.toString())
        }

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
                body { row(magenta("Total Manifest permissions"), totalPermissions) }
                body { row(magenta("Dangerous permissions"), dangerousPermissions.asAffirmation()) }
            }
        }
        terminal.emptyLine()
        terminal.println(content)
        terminal.emptyLine()
    }

    private fun Boolean.asAffirmation() = if (this) "Yes" else "No"

    private const val PLACEHOLDER_APP_ID = "APPLICATION_ID"
    private const val PLACEHOLDER_MIN_SDK = "MIN_SDK"
    private const val PLACEHOLDER_TARGET_SDK = "TARGET_SDK"
    private const val PLACEHOLDER_TOTAL_PERMISSIONS = "TOTAL_PERMS"
    private const val PLACEHOLDER_SENSITIVE_PERMISSIONS = "SENSITIVE_PERMS"
    private const val PLACEHOLDER_DEBUGGABLE = "DEBUGGABLE"

    private val jsonTemplate =
        """
        {
            "app_id":"$PLACEHOLDER_APP_ID",
            "min_sdk":$PLACEHOLDER_MIN_SDK,
            "target_sdk":$PLACEHOLDER_TARGET_SDK,
            "total_manifest_permissions":$PLACEHOLDER_TOTAL_PERMISSIONS,
            "uses_dangerous_permissions":$PLACEHOLDER_SENSITIVE_PERMISSIONS,
            "debuggable":$PLACEHOLDER_DEBUGGABLE
        }
        """.trimIndent()
}
