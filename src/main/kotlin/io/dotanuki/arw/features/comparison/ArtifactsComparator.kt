package io.dotanuki.arw.features.comparison

import io.dotanuki.arw.core.domain.models.AnalysedArtifact
import io.dotanuki.arw.core.domain.models.AndroidComponentType

object ArtifactsComparator {

    fun compare(target: AnalysedArtifact, baseline: AnalysedArtifact): Set<ComparisonFinding> {
        val permissions = evaluateChanges(
            target.androidPermissions,
            baseline.androidPermissions
        ).map { (what, expectation) ->
            ComparisonFinding(what, expectation, FindingCategory.PERMISSION)
        }

        val features = evaluateChanges(
            target.androidFeatures,
            baseline.androidFeatures
        ).map { (what, expectation) ->
            ComparisonFinding(what, expectation, FindingCategory.FEATURE)
        }

        val activities = evaluateChanges(
            target.filterComponent(AndroidComponentType.ACTIVITY),
            baseline.filterComponent(AndroidComponentType.ACTIVITY)
        ).map { (what, expectation) ->
            ComparisonFinding(what, expectation, FindingCategory.COMPONENT)
        }

        val services = evaluateChanges(
            target.filterComponent(AndroidComponentType.SERVICE),
            baseline.filterComponent(AndroidComponentType.SERVICE)
        ).map { (what, expectation) ->
            ComparisonFinding(what, expectation, FindingCategory.COMPONENT)
        }

        val receivers = evaluateChanges(
            target.filterComponent(AndroidComponentType.RECEIVER),
            baseline.filterComponent(AndroidComponentType.RECEIVER)
        ).map { (what, finding) ->
            ComparisonFinding(what, finding, FindingCategory.COMPONENT)
        }

        val providers = evaluateChanges(
            target.filterComponent(AndroidComponentType.PROVIDER),
            baseline.filterComponent(AndroidComponentType.PROVIDER)
        ).map { (what, expectation) ->
            ComparisonFinding(what, expectation, FindingCategory.COMPONENT)
        }

        return permissions + features + activities + services + receivers union providers
    }

    private fun evaluateChanges(target: Set<String>, baseline: Set<String>): Set<Pair<String, BrokenExpectation>> {
        val missingOnBaseline = (target subtract baseline).map {
            it to BrokenExpectation.MISSING_ON_BASELINE
        }

        val missingOnTarget = (baseline subtract target).map {
            it to BrokenExpectation.MISSING_ON_ARTIFACT
        }

        return missingOnBaseline union missingOnTarget
    }
}
