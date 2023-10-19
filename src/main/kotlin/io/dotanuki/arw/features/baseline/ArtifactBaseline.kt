package io.dotanuki.arw.features.baseline

import kotlinx.serialization.Serializable

@Serializable
data class ArtifactBaseline(
    val permissions: Set<String>,
    val features: Set<String>,
    val activities: List<String>,
    val services: List<String> = emptyList(),
    val receivers: List<String> = emptyList(),
    val providers: List<String> = emptyList()
)
