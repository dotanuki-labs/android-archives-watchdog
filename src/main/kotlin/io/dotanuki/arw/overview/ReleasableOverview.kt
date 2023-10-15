package io.dotanuki.arw.overview

data class ReleasableOverview(
    val applicationId: String,
    val minSdk: Int,
    val targetSdk: Int,
    val totalPermissions: Int,
    val dangerousPermissions: Boolean,
    val debuggable: Boolean
)
