package io.dotanuki.arw.features.overview

data class ReleasableOverview(
    val applicationId: String,
    val minSdk: Int,
    val targetSdk: Int,
    val totalPermissions: Int,
    val dangerousPermissions: Boolean,
    val debuggable: Boolean
)
