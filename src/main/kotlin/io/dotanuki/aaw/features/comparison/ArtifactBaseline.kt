package io.dotanuki.aaw.features.comparison

import io.dotanuki.aaw.core.android.AndroidComponent

data class ArtifactBaseline(
    val applicationId: String,
    val androidPermissions: Set<String>,
    val androidFeatures: Set<String>,
    val androidComponents: Set<AndroidComponent>,
    val trustedPackages: Set<String> = emptySet()
)
