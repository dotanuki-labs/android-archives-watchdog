package io.dotanuki.arw.core.domain.models

data class AnalysedArtifact(
    val applicationId: String,
    val minSdk: Int,
    val targetSdk: Int,
    val androidPermissions: Set<String>,
    val androidFeatures: Set<String>,
    val androidComponents: Set<AndroidComponent>
) {
    fun filterComponent(componentType: AndroidComponentType): Set<String> =
        androidComponents.filter { it.type == componentType }.map { it.name }.toSet()
}
