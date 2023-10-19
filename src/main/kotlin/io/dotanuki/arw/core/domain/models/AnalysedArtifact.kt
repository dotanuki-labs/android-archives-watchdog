package io.dotanuki.arw.core.domain.models

data class AnalysedArtifact(
    val applicationId: String,
    val minSdk: Int,
    val targetSdk: Int,
    val debuggable: Boolean,
    val androidPermissions: Set<String>,
    val androidFeatures: Set<String>,
    val androidComponents: List<AndroidComponent>
)
