package io.dotanuki.arw.features.comparison

import io.dotanuki.arw.core.android.AnalysedArtifact
import io.dotanuki.arw.core.android.AndroidComponentType
import io.dotanuki.arw.core.android.declaredNames
import io.dotanuki.arw.core.errors.ArwError
import io.dotanuki.arw.core.errors.ErrorAware

object ArtifactsComparator {

    context (ErrorAware)
    fun compare(target: AnalysedArtifact, baseline: ArtifactBaseline): Set<ComparisonFinding> {
        ensureSameApplication(target, baseline)

        val permissions = evaluateChanges(
            target.androidPermissions,
            baseline.androidPermissions,
            baseline.trustedPackages
        ).map { (what, expectation) ->
            ComparisonFinding(what, expectation, FindingCategory.PERMISSION)
        }

        val features = evaluateChanges(
            target.androidFeatures,
            baseline.androidFeatures,
            baseline.trustedPackages
        ).map { (what, expectation) ->
            ComparisonFinding(what, expectation, FindingCategory.FEATURE)
        }

        val activities = evaluateChanges(
            target.androidComponents.declaredNames(AndroidComponentType.ACTIVITY),
            baseline.androidComponents.declaredNames(AndroidComponentType.ACTIVITY),
            baseline.trustedPackages
        ).map { (what, expectation) ->
            ComparisonFinding(what, expectation, FindingCategory.COMPONENT)
        }

        val services = evaluateChanges(
            target.androidComponents.declaredNames(AndroidComponentType.SERVICE),
            baseline.androidComponents.declaredNames(AndroidComponentType.SERVICE),
            baseline.trustedPackages
        ).map { (what, expectation) ->
            ComparisonFinding(what, expectation, FindingCategory.COMPONENT)
        }

        val receivers = evaluateChanges(
            target.androidComponents.declaredNames(AndroidComponentType.RECEIVER),
            baseline.androidComponents.declaredNames(AndroidComponentType.RECEIVER),
            baseline.trustedPackages
        ).map { (what, finding) ->
            ComparisonFinding(what, finding, FindingCategory.COMPONENT)
        }

        val providers = evaluateChanges(
            target.androidComponents.declaredNames(AndroidComponentType.PROVIDER),
            baseline.androidComponents.declaredNames(AndroidComponentType.PROVIDER),
            baseline.trustedPackages
        ).map { (what, expectation) ->
            ComparisonFinding(what, expectation, FindingCategory.COMPONENT)
        }

        return permissions + features + activities + services + receivers union providers
    }

    context (ErrorAware)
    private fun ensureSameApplication(target: AnalysedArtifact, baseline: ArtifactBaseline) {
        if (target.applicationId != baseline.applicationId) {
            val description = """
            Your application packages dont match !!!
            From your artifact : ${target.applicationId}
            From your baseline : ${baseline.applicationId}
            """.trimIndent()

            raise(ArwError(description))
        }
    }

    private fun evaluateChanges(
        fromArtifact: Set<String>,
        fromBaseline: Set<String>,
        trustedPackages: Set<String>
    ): Set<Pair<String, BrokenExpectation>> {
        val sanitizedPrefixes = trustedPackages.map { it.replace(".*", "") }

        val missingOnBaseline = (fromArtifact subtract fromBaseline)
            .filterNot { it.prefixedWithAny(sanitizedPrefixes) }
            .map { it to BrokenExpectation.MISSING_ON_BASELINE }
            .toSet()

        val missingOnTarget = (fromBaseline subtract fromArtifact)
            .filterNot { it.prefixedWithAny(sanitizedPrefixes) }
            .map { it to BrokenExpectation.MISSING_ON_ARTIFACT }
            .toSet()

        return missingOnBaseline union missingOnTarget
    }

    private fun String.prefixedWithAny(patterns: List<String>): Boolean {
        patterns.forEach {
            if (startsWith(it)) return true
        }

        return false
    }
}
