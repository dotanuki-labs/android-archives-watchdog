package io.dotanuki.arw.features.baseline

import kotlinx.serialization.Serializable

@Serializable
data class ArtifactBaseline(
    val permissions: Set<String>,
    val features: Set<String>
)
