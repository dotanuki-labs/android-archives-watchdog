/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.comparison

import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.table.table

object ComparisonReporter {

    private const val OUTCOME_NO_CHANGES = "No changes detected"
    private const val OUTCOME_CHANGES_DETECTED = "Your baseline file does not match the supplied artifact"

    context (CompareContext)
    fun reportChanges(comparison: Set<ComparisonFinding>, format: String) {
        when (format) {
            "console" -> reportAsText(comparison)
            "json" -> reportAsJson(comparison)
        }
    }

    context (CompareContext)
    private fun reportAsJson(comparison: Set<ComparisonFinding>) {
        val outcome = when {
            comparison.isEmpty() -> OUTCOME_NO_CHANGES
            else -> OUTCOME_CHANGES_DETECTED
        }

        val results = comparison.map {
            ComparisonResult(
                item = it.what,
                category = it.category.description,
                finding = "Missing at ${it.expectation.description()}"
            )
        }

        val serializable = SerializableComparison(outcome, results)
        val jsonContent = jsonSerializer.encodeToString(SerializableComparison.serializer(), serializable)
        logger.info(jsonContent)
    }

    context (CompareContext)
    private fun reportAsText(comparison: Set<ComparisonFinding>) {
        if (comparison.isEmpty()) {
            logger.newLine()
            terminal.println(OUTCOME_NO_CHANGES)
            logger.newLine()
            return
        }

        logger.newLine()
        logger.info(OUTCOME_CHANGES_DETECTED)
        logger.newLine()

        val changeAsTable = table {
            header { row(cyan("Category"), cyan("Finding"), cyan("Missing at")) }
            comparison.map {
                body {
                    row(it.category.description, it.what, it.expectation.description())
                }
            }
        }

        logger.info(changeAsTable)
        logger.newLine()
        logger.info("Please update your baseline accordingly")
    }

    context (CompareContext)
    private fun BrokenExpectation.description() = when (this) {
        BrokenExpectation.MISSING_ON_BASELINE -> "Baseline"
        BrokenExpectation.MISSING_ON_ARTIFACT -> "Archive"
    }
}
