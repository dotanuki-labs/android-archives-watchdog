package io.dotanuki.arw.overview

data class ReleasableOverview(
    val applicationId: String,
    val minSdk: Int,
    val targetSdk: Int,
    val totalPermissions: Int,
    val sensitivePermissions: Boolean,
    val debuggable: Boolean
)
