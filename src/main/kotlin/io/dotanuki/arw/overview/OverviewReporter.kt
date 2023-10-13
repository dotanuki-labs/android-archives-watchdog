package io.dotanuki.arw.overview

import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.rendering.TextColors.magenta
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import io.dotanuki.arw.utils.emptyLine

class OverviewReporter {

    private val terminal by lazy { Terminal() }

    fun report(overview: ReleasableOverview, format: String) {
        when (format) {
            "text" -> reportAsText(overview)
            "json" -> reportAsJson(overview)
        }
    }

    private fun reportAsJson(overview: ReleasableOverview) {
        val content = with(overview) {
            jsonTemplate
                .replace(PLACEHOLDER_ARCHIVE_TYPE, type.name.lowercase())
                .replace(PLACEHOLDER_MIN_SDK, minSdk.toString())
                .replace(PLACEHOLDER_TARGET_SDK, targetSdk.toString())
                .replace(PLACEHOLDER_TOTAL_PERMISSIONS, totalPermissions.toString())
                .replace(PLACEHOLDER_SENSITIVE_PERMISSIONS, sensitivePermissions.toString())
        }

        terminal.println(content)
    }

    private fun reportAsText(overview: ReleasableOverview) {
        val content = with(overview) {
            table {
                header { row(cyan("Attribute"), cyan("Evaluation")) }
                body { row(magenta("Archive Type"), type.description) }
                body { row(magenta("Minimum SDK"), minSdk) }
                body { row(magenta("Target SDK"), targetSdk) }
                body { row(magenta("Total Manifest permissions"), totalPermissions) }
                body { row(magenta("Sensitive permissions"), sensitivePermissions.asAffirmation()) }
            }
        }
        terminal.emptyLine()
        terminal.println(content)
        terminal.emptyLine()
    }

    private fun Boolean.asAffirmation() = if (this) "Yes" else "No"

    private companion object {

        const val PLACEHOLDER_ARCHIVE_TYPE = "ARCHIVE_TYPE"
        const val PLACEHOLDER_MIN_SDK = "MIN_SDK"
        const val PLACEHOLDER_TARGET_SDK = "TARGET_SDK"
        const val PLACEHOLDER_TOTAL_PERMISSIONS = "TOTAL_PERMS"
        const val PLACEHOLDER_SENSITIVE_PERMISSIONS = "SENSITIVE_PERMS"

        val jsonTemplate =
            """
            {
                "archive_type":"$PLACEHOLDER_ARCHIVE_TYPE",
                "min_sdk":$PLACEHOLDER_MIN_SDK,
                "target_sdk":$PLACEHOLDER_TARGET_SDK,
                "total_manifest_permissions":$PLACEHOLDER_TOTAL_PERMISSIONS,
                "uses_sensitive_permissions":$PLACEHOLDER_SENSITIVE_PERMISSIONS
            }
            """.trimIndent()
    }
}
