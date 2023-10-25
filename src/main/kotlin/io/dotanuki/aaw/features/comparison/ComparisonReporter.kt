package io.dotanuki.aaw.features.comparison

import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.table.table
import io.dotanuki.aaw.core.cli.ExitCodes
import io.dotanuki.aaw.core.cli.emptyLine
import kotlin.system.exitProcess

object ComparisonReporter {

    context (CompareContext)
    fun reportChanges(comparison: Set<ComparisonFinding>) {
        if (comparison.isEmpty()) {
            terminal.emptyLine()
            terminal.println("No changes detected")
            terminal.emptyLine()
            return
        }

        terminal.emptyLine()
        terminal.println("Your baseline file does not match the supplied artifact !!!")
        terminal.emptyLine()

        val changeAsTable = table {
            header { row(cyan("Category"), cyan("Finding"), cyan("Description")) }
            comparison.map {
                body {
                    row(it.category.description, it.what, it.expectation.description())
                }
            }
        }

        terminal.println(changeAsTable)
        terminal.emptyLine()
        terminal.println("Please update your baseline accordingly")
        exitProcess(ExitCodes.FAILURE)
    }

    context (CompareContext)
    private fun BrokenExpectation.description() = when (this) {
        BrokenExpectation.MISSING_ON_BASELINE -> "Missing on your baseline file"
        BrokenExpectation.MISSING_ON_ARTIFACT -> "Not found in your artifact"
    }
}
