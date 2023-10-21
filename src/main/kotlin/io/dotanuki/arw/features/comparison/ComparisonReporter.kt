package io.dotanuki.arw.features.comparison

import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.table.table
import io.dotanuki.arw.core.infrastructure.cli.emptyLine
import kotlin.system.exitProcess

object ComparisonReporter {

    context (CompareContext)
    fun reportChanges(comparison: Set<ComparisonOutcome>) {
        if (comparison.isEmpty()) {
            terminal.emptyLine()
            terminal.println("No changes detected")
            terminal.emptyLine()
            return
        }

        terminal.emptyLine()
        terminal.println("Your baseline file does not match the supplied artifact")
        terminal.emptyLine()

        val changeAsTable = table {
            header { row(cyan("What"), cyan("Finding")) }
            comparison.map {
                body {
                    row(it.what, it.comparisonFinding.recommendation())
                }
            }
        }

        terminal.println(changeAsTable)
        terminal.emptyLine()
        exitProcess(42)
    }

    context (CompareContext)
    private fun ComparisonFinding.recommendation() = when (this) {
        ComparisonFinding.MISSING_ON_BASELINE -> "Missing on your baseline file"
        ComparisonFinding.MISSING_ON_ARTIFACT -> "Not present in your artifact "
    }
}
