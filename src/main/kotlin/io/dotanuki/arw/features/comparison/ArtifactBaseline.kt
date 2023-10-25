package io.dotanuki.arw.features.comparison

import io.dotanuki.arw.core.android.AndroidComponent

data class ArtifactBaseline(
    val applicationId: String,
    val androidPermissions: Set<String>,
    val androidFeatures: Set<String>,
    val androidComponents: Set<AndroidComponent>,
    val trustedPackages: Set<String> = emptySet()
)
