package io.dotanuki.arw.features.comparison

import com.google.common.truth.Truth.assertThat
import io.dotanuki.arw.core.domain.models.AnalysedArtifact
import io.dotanuki.arw.core.domain.models.AndroidComponent
import io.dotanuki.arw.core.domain.models.AndroidComponentType.ACTIVITY
import io.dotanuki.arw.core.domain.models.AndroidComponentType.APPLICATION
import io.dotanuki.arw.core.domain.models.AndroidComponentType.PROVIDER
import io.dotanuki.arw.core.domain.models.AndroidComponentType.RECEIVER
import io.dotanuki.arw.core.domain.models.AndroidComponentType.SERVICE
import org.junit.Test

class ArtifactsComparatorTests {

    private val referenceArtifact = AnalysedArtifact(
        applicationId = "io.dotanuki.norris.android",
        minSdk = 28,
        targetSdk = 33,
        androidPermissions = setOf(
            "android.permission.INTERNET",
            "io.dotanuki.norris.android.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
        ),
        androidFeatures = setOf(
            "android.hardware.screen.portrait",
            "android.hardware.faketouch"
        ),
        androidComponents = setOf(
            AndroidComponent("io.dotanuki.app.SplashActivity", ACTIVITY),
            AndroidComponent("io.dotanuki.features.facts.ui.FactsActivity", ACTIVITY),
            AndroidComponent("io.dotanuki.features.search.ui.SearchActivity", ACTIVITY),
            AndroidComponent("io.dotanuki.app.NorrisApplication", APPLICATION),
            AndroidComponent("androidx.startup.InitializationProvider", PROVIDER),
            AndroidComponent("androidx.profileinstaller.ProfileInstallReceiver", RECEIVER)
        )
    )

    @Test fun `should detect no changes when comparing the same artefact`() {
        val comparison = ArtifactsComparator.compare(referenceArtifact, referenceArtifact)
        assertThat(comparison).isEmpty()
    }

    @Test fun `should detect a new permission added on target artifact`() {
        val cameraPermission = "android.permission.CAMERA"
        val newPermissions = referenceArtifact.androidPermissions + "android.permission.CAMERA"
        val subject = referenceArtifact.copy(androidPermissions = newPermissions)

        val comparison = ArtifactsComparator.compare(subject, referenceArtifact)

        val expected = setOf(
            DetectedChange(cameraPermission, ProposedAction.ADD_TO_BASELINE)
        )

        assertThat(comparison).isEqualTo(expected)
    }

    @Test fun `should detect a permission removed from target artifact`() {
        val removedPermission = "io.dotanuki.norris.android.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
        val newPermissions = referenceArtifact.androidPermissions - removedPermission
        val subject = referenceArtifact.copy(androidPermissions = newPermissions)

        val comparison = ArtifactsComparator.compare(subject, referenceArtifact)

        val expected = setOf(
            DetectedChange(removedPermission, ProposedAction.REMOVE_FROM_BASELINE)
        )

        assertThat(comparison).isEqualTo(expected)
    }

    @Test fun `should detect a new service added in the target artifact`() {
        val serviceName = "io.dotanuki.app.MediaPlayerService"
        val missingService = AndroidComponent(serviceName, SERVICE)
        val withMissingService = referenceArtifact.androidComponents + missingService
        val newReference = referenceArtifact.copy(androidComponents = withMissingService)

        val comparison = ArtifactsComparator.compare(newReference, referenceArtifact)

        val expected = setOf(
            DetectedChange(serviceName, ProposedAction.ADD_TO_BASELINE)
        )

        assertThat(comparison).isEqualTo(expected)
    }

    @Test fun `should detect an invalid activity tracked on the baseline`() {
        val activityName = "io.dotanuki.app.DeepLinkActivity"
        val dereferencedActivity = AndroidComponent(activityName, ACTIVITY)
        val withDereferencedActivity = referenceArtifact.androidComponents + dereferencedActivity
        val newReference = referenceArtifact.copy(androidComponents = withDereferencedActivity)

        val comparison = ArtifactsComparator.compare(referenceArtifact, newReference)

        val expected = setOf(
            DetectedChange(activityName, ProposedAction.REMOVE_FROM_BASELINE)
        )

        assertThat(comparison).isEqualTo(expected)
    }

    @Test fun `should detect multiple changes`() {
        val serviceName = "io.dotanuki.app.TrackingService"
        val missingService = AndroidComponent(serviceName, SERVICE)

        val addedPermission = "android.permission.WRITE_EXTERNAL_STORAGE"
        val newPermissions = referenceArtifact.androidPermissions + addedPermission

        val newComponents = referenceArtifact.androidComponents + missingService
        val newReference = referenceArtifact.copy(
            androidComponents = newComponents,
            androidPermissions = newPermissions
        )

        val comparison = ArtifactsComparator.compare(newReference, referenceArtifact)

        val expected = setOf(
            DetectedChange(addedPermission, ProposedAction.ADD_TO_BASELINE),
            DetectedChange(serviceName, ProposedAction.ADD_TO_BASELINE)
        )

        assertThat(comparison).isEqualTo(expected)
    }
}
