/*
 * Copyright 2025 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.toml

import io.dotanuki.aaw.core.android.AnalysedArtifact
import io.dotanuki.aaw.core.android.AndroidComponent
import io.dotanuki.aaw.core.android.AndroidComponentType.ACTIVITY
import io.dotanuki.aaw.core.android.AndroidComponentType.PROVIDER
import io.dotanuki.aaw.core.android.AndroidComponentType.RECEIVER
import io.dotanuki.aaw.core.android.AndroidComponentType.SERVICE
import io.dotanuki.aaw.core.android.declaredNames
import io.dotanuki.aaw.features.comparison.ArtifactBaseline
import kotlinx.serialization.Serializable

@Serializable
data class WatchdogConfig(
    val applicationId: String,
    val permissions: Set<String> = emptySet(),
    val features: Set<String> = emptySet(),
    val trustedPackages: Set<String> = emptySet(),
    val activities: Set<String> = emptySet(),
    val services: Set<String> = emptySet(),
    val receivers: Set<String> = emptySet(),
    val providers: Set<String> = emptySet(),
) {
    fun asBaseline(): ArtifactBaseline =
        ArtifactBaseline(
            applicationId,
            permissions,
            features,
            aggregateComponents().toSet(),
            trustedPackages,
        )

    private fun aggregateComponents() =
        activities.map { AndroidComponent(it, ACTIVITY) } +
            services.map { AndroidComponent(it, SERVICE) } +
            receivers.map { AndroidComponent(it, RECEIVER) } +
            providers.map { AndroidComponent(it, PROVIDER) }

    companion object {
        fun from(
            analysed: AnalysedArtifact,
            packagesToIgnore: List<String>,
        ) = with(analysed) {
            WatchdogConfig(
                applicationId,
                trustedPackages = packagesToIgnore.toSortedSet(),
                permissions = androidPermissions,
                features = androidFeatures,
                activities = androidComponents.declaredNames(ACTIVITY, packagesToIgnore),
                services = androidComponents.declaredNames(SERVICE, packagesToIgnore),
                receivers = androidComponents.declaredNames(RECEIVER, packagesToIgnore),
                providers = androidComponents.declaredNames(PROVIDER, packagesToIgnore),
            )
        }
    }
}
