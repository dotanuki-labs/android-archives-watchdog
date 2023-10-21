package io.dotanuki.arw.features.common

import io.dotanuki.arw.core.domain.models.AnalysedArtifact
import io.dotanuki.arw.core.domain.models.AndroidComponent
import io.dotanuki.arw.core.domain.models.AndroidComponentType.ACTIVITY
import io.dotanuki.arw.core.domain.models.AndroidComponentType.PROVIDER
import io.dotanuki.arw.core.domain.models.AndroidComponentType.RECEIVER
import io.dotanuki.arw.core.domain.models.AndroidComponentType.SERVICE
import kotlinx.serialization.Serializable

@Serializable
data class ArtifactBaseline(
    val applicationId: String,
    val minSdk: Int,
    val targetSdk: Int,
    val permissions: Set<String>,
    val features: Set<String>,
    val activities: Set<String>,
    val services: Set<String> = emptySet(),
    val receivers: Set<String> = emptySet(),
    val providers: Set<String> = emptySet()
) {
    fun asArtifact(): AnalysedArtifact =
        AnalysedArtifact(
            applicationId,
            minSdk,
            targetSdk,
            permissions,
            features,
            aggregateComponents()
        )

    private fun aggregateComponents() =
        activities.map { AndroidComponent(it, ACTIVITY) }.toSet() +
            services.map { AndroidComponent(it, SERVICE) }.toSet() +
            receivers.map { AndroidComponent(it, RECEIVER) }.toSet() +
            providers.map { AndroidComponent(it, PROVIDER) }.toSet()

    companion object {
        fun from(analysed: AnalysedArtifact) = with(analysed) {
            ArtifactBaseline(
                applicationId,
                minSdk,
                targetSdk,
                androidPermissions,
                androidFeatures,
                filterComponent(ACTIVITY),
                filterComponent(SERVICE),
                filterComponent(RECEIVER),
                filterComponent(PROVIDER)
            )
        }
    }
}
