package io.dotanuki.aaw.core.android

data class AnalysedArtifact(
    val applicationId: String,
    val minSdk: Int,
    val targetSdk: Int,
    val androidPermissions: Set<String>,
    val androidFeatures: Set<String>,
    val androidComponents: Set<AndroidComponent>
)
