package io.dotanuki.arw.features.overview

data class ArtifactOverview(
    val applicationId: String,
    val minSdk: Int,
    val targetSdk: Int,
    val debuggable: Boolean,
    val totalPermissions: Int,
    val dangerousPermissions: Boolean
)
