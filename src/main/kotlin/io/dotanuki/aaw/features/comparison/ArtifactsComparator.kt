/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.comparison

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.dotanuki.aaw.core.android.AnalysedArtifact
import io.dotanuki.aaw.core.android.AndroidComponentType
import io.dotanuki.aaw.core.android.declaredNames
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.logging.Logger

@Suppress("LongMethod")
class ArtifactsComparator(
    private val logger: Logger,
) {
    fun compare(
        target: AnalysedArtifact,
        baseline: ArtifactBaseline,
    ): Either<AawError, Set<ComparisonFinding>> {
        if (target.applicationId != baseline.applicationId) {
            val description =
                """
                Your application packages dont match !!!
                From your artifact : ${target.applicationId}
                From your baseline : ${baseline.applicationId}
                """.trimIndent()
            return AawError(description).left()
        }

        val permissions =
            evaluateChanges(
                target.androidPermissions,
                baseline.androidPermissions,
                baseline.trustedPackages,
                "permissions",
            ).map { (what, expectation) ->
                ComparisonFinding(what, expectation, FindingCategory.PERMISSION)
            }

        val features =
            evaluateChanges(
                target.androidFeatures,
                baseline.androidFeatures,
                baseline.trustedPackages,
                "system capabilities",
            ).map { (what, expectation) ->
                ComparisonFinding(what, expectation, FindingCategory.FEATURE)
            }

        val activities =
            evaluateChanges(
                target.androidComponents.declaredNames(AndroidComponentType.ACTIVITY),
                baseline.androidComponents.declaredNames(AndroidComponentType.ACTIVITY),
                baseline.trustedPackages,
                "Activities",
            ).map { (what, expectation) ->
                ComparisonFinding(what, expectation, FindingCategory.COMPONENT)
            }

        val services =
            evaluateChanges(
                target.androidComponents.declaredNames(AndroidComponentType.SERVICE),
                baseline.androidComponents.declaredNames(AndroidComponentType.SERVICE),
                baseline.trustedPackages,
                "Services",
            ).map { (what, expectation) ->
                ComparisonFinding(what, expectation, FindingCategory.COMPONENT)
            }

        val receivers =
            evaluateChanges(
                target.androidComponents.declaredNames(AndroidComponentType.RECEIVER),
                baseline.androidComponents.declaredNames(AndroidComponentType.RECEIVER),
                baseline.trustedPackages,
                "Broadcast Receivers",
            ).map { (what, finding) ->
                ComparisonFinding(what, finding, FindingCategory.COMPONENT)
            }

        val providers =
            evaluateChanges(
                target.androidComponents.declaredNames(AndroidComponentType.PROVIDER),
                baseline.androidComponents.declaredNames(AndroidComponentType.PROVIDER),
                baseline.trustedPackages,
                "Content Providers",
            ).map { (what, expectation) ->
                ComparisonFinding(what, expectation, FindingCategory.COMPONENT)
            }

        val all = permissions + features + activities + services + receivers union providers
        return all.right()
    }

    private fun evaluateChanges(
        fromArtifact: Set<String>,
        fromBaseline: Set<String>,
        trustedPackages: Set<String>,
        subject: String,
    ): Set<Pair<String, BrokenExpectation>> {
        logger.debug("Evaluating missing $subject on the baseline file")
        val missingOnBaseline =
            (fromArtifact subtract fromBaseline)
                .filterNot { it.prefixedWithAny(trustedPackages) }
                .map { it to BrokenExpectation.MISSING_ON_BASELINE }
                .toSet()

        logger.debug("Evaluating missing $subject target archive")
        val missingOnTarget =
            (fromBaseline subtract fromArtifact)
                .filterNot { it.prefixedWithAny(trustedPackages) }
                .map { it to BrokenExpectation.MISSING_ON_ARTIFACT }
                .toSet()

        return missingOnBaseline union missingOnTarget
    }

    private fun String.prefixedWithAny(patterns: Set<String>): Boolean {
        patterns.forEach {
            if (startsWith(it)) return true
        }

        return false
    }
}
