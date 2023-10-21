package io.dotanuki.arw.features.comparison

import io.dotanuki.arw.core.domain.models.AnalysedArtifact
import io.dotanuki.arw.core.domain.models.AndroidComponentType

object ArtifactsComparator {

    fun compare(target: AnalysedArtifact, baseline: AnalysedArtifact): Set<ComparisonOutcome> {
        val permissionsDiff = evaluateChanges(
            target.androidPermissions,
            baseline.androidPermissions
        )

        val featuresDiff = evaluateChanges(
            target.androidFeatures,
            baseline.androidFeatures
        )

        val activitiesDiff = evaluateChanges(
            target.filterComponent(AndroidComponentType.ACTIVITY),
            baseline.filterComponent(AndroidComponentType.ACTIVITY)
        )

        val servicesDiff = evaluateChanges(
            target.filterComponent(AndroidComponentType.SERVICE),
            baseline.filterComponent(AndroidComponentType.SERVICE)
        )

        val receiversDiff = evaluateChanges(
            target.filterComponent(AndroidComponentType.RECEIVER),
            baseline.filterComponent(AndroidComponentType.RECEIVER)
        )

        val providersDiff = evaluateChanges(
            target.filterComponent(AndroidComponentType.PROVIDER),
            baseline.filterComponent(AndroidComponentType.PROVIDER)
        )

        return permissionsDiff + featuresDiff + activitiesDiff + servicesDiff + receiversDiff + providersDiff
    }

    private fun evaluateChanges(target: Set<String>, baseline: Set<String>): Set<ComparisonOutcome> {
        val missingOnBaseline = (target subtract baseline).map {
            ComparisonOutcome(it, ComparisonFinding.MISSING_ON_BASELINE)
        }

        val missingOnTarget = (baseline subtract target).map {
            ComparisonOutcome(it, ComparisonFinding.MISSING_ON_ARTIFACT)
        }

        return missingOnBaseline union missingOnTarget
    }
}
