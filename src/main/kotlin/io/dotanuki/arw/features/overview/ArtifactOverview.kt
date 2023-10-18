package io.dotanuki.arw.features.overview

import kotlinx.serialization.Serializable

@Serializable
data class ArtifactOverview(
    val applicationId: String,
    val minSdk: Int,
    val targetSdk: Int,
    val debuggable: Boolean,
    val totalUsedFeatures: Int,
    val totalPermissions: Int,
    val dangerousPermissions: Boolean
)
