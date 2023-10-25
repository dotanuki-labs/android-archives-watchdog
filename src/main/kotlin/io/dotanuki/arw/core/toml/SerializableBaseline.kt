package io.dotanuki.arw.core.toml

import io.dotanuki.arw.core.android.AnalysedArtifact
import io.dotanuki.arw.core.android.AndroidComponent
import io.dotanuki.arw.core.android.AndroidComponentType.ACTIVITY
import io.dotanuki.arw.core.android.AndroidComponentType.PROVIDER
import io.dotanuki.arw.core.android.AndroidComponentType.RECEIVER
import io.dotanuki.arw.core.android.AndroidComponentType.SERVICE
import kotlinx.serialization.Serializable

@Serializable
data class SerializableBaseline(
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
            SerializableBaseline(
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
