/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.comparison

import arrow.core.raise.recover
import com.google.common.truth.Truth.assertThat
import io.dotanuki.aaw.core.android.AnalysedArtifact
import io.dotanuki.aaw.core.android.AndroidComponent
import io.dotanuki.aaw.core.android.AndroidComponentType.ACTIVITY
import io.dotanuki.aaw.core.android.AndroidComponentType.APPLICATION
import io.dotanuki.aaw.core.android.AndroidComponentType.PROVIDER
import io.dotanuki.aaw.core.android.AndroidComponentType.RECEIVER
import io.dotanuki.aaw.core.android.AndroidComponentType.SERVICE
import io.dotanuki.aaw.core.logging.Logging
import io.dotanuki.aaw.helpers.errorAwareTest
import org.junit.Test

class ArtifactsComparatorTests {
    private val comparator by lazy {
        with(Logging.create()) {
            ArtifactsComparator()
        }
    }

    private val referenceArtifact =
        AnalysedArtifact(
            applicationId = "io.dotanuki.norris.android",
            minSdk = 28,
            targetSdk = 33,
            androidPermissions =
                setOf(
                    "android.permission.INTERNET",
                    "io.dotanuki.norris.android.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
                ),
            androidFeatures =
                setOf(
                    "android.hardware.screen.portrait",
                    "android.hardware.faketouch",
                ),
            androidComponents =
                setOf(
                    AndroidComponent("io.dotanuki.app.SplashActivity", ACTIVITY),
                    AndroidComponent("io.dotanuki.features.facts.ui.FactsActivity", ACTIVITY),
                    AndroidComponent("io.dotanuki.features.search.ui.SearchActivity", ACTIVITY),
                    AndroidComponent("io.dotanuki.app.NorrisApplication", APPLICATION),
                    AndroidComponent("androidx.startup.InitializationProvider", PROVIDER),
                    AndroidComponent("androidx.profileinstaller.ProfileInstallReceiver", RECEIVER),
                ),
        )

    private val completeBaseline =
        ArtifactBaseline(
            applicationId = "io.dotanuki.norris.android",
            androidPermissions =
                setOf(
                    "android.permission.INTERNET",
                    "io.dotanuki.norris.android.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
                ),
            androidFeatures =
                setOf(
                    "android.hardware.screen.portrait",
                    "android.hardware.faketouch",
                ),
            androidComponents =
                setOf(
                    AndroidComponent("io.dotanuki.app.SplashActivity", ACTIVITY),
                    AndroidComponent("io.dotanuki.features.facts.ui.FactsActivity", ACTIVITY),
                    AndroidComponent("io.dotanuki.features.search.ui.SearchActivity", ACTIVITY),
                    AndroidComponent("io.dotanuki.app.NorrisApplication", APPLICATION),
                    AndroidComponent("androidx.startup.InitializationProvider", PROVIDER),
                    AndroidComponent("androidx.profileinstaller.ProfileInstallReceiver", RECEIVER),
                ),
        )

    private val compactBaseline =
        ArtifactBaseline(
            applicationId = "io.dotanuki.norris.android",
            trustedPackages = setOf("io.dotanuki"),
            androidPermissions =
                setOf(
                    "android.permission.INTERNET",
                ),
            androidFeatures =
                setOf(
                    "android.hardware.screen.portrait",
                    "android.hardware.faketouch",
                ),
            androidComponents =
                setOf(
                    AndroidComponent("androidx.startup.InitializationProvider", PROVIDER),
                    AndroidComponent("androidx.profileinstaller.ProfileInstallReceiver", RECEIVER),
                ),
        )

    @Test fun `should detect no changes when comparing with a complete baseline`() =
        errorAwareTest {
            val comparison = comparator.compare(referenceArtifact, completeBaseline)
            assertThat(comparison).isEmpty()
        }

    @Test fun `should detect no changes when comparing with a compact baseline`() =
        errorAwareTest {
            val comparison = comparator.compare(referenceArtifact, compactBaseline)
            assertThat(comparison).isEmpty()
        }

    @Test fun `should reject artefacts from different application`() {
        val debugVersion = referenceArtifact.copy(applicationId = "io.dotanuki.norris.android.debug")
        recover(
            block = { comparator.compare(debugVersion, compactBaseline) },
            recover = { surfaced ->

                assertThat(surfaced.description).contains("Your application packages dont match")
            },
        )
    }

    @Test fun `should detect a new permission added on target artifact`() =
        errorAwareTest {
            val cameraPermission = "android.permission.CAMERA"
            val newPermissions = referenceArtifact.androidPermissions + "android.permission.CAMERA"
            val subject = referenceArtifact.copy(androidPermissions = newPermissions)

            val comparison = comparator.compare(subject, completeBaseline)

            val expected =
                setOf(
                    ComparisonFinding(
                        cameraPermission,
                        BrokenExpectation.MISSING_ON_BASELINE,
                        FindingCategory.PERMISSION,
                    ),
                )

            assertThat(comparison).isEqualTo(expected)
        }

    @Test fun `should detect a permission removed from target artifact`() =
        errorAwareTest {
            val removedPermission = "io.dotanuki.norris.android.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
            val newPermissions = referenceArtifact.androidPermissions - removedPermission
            val subject = referenceArtifact.copy(androidPermissions = newPermissions)

            val comparison = comparator.compare(subject, completeBaseline)

            val expected =
                setOf(
                    ComparisonFinding(
                        removedPermission,
                        BrokenExpectation.MISSING_ON_ARTIFACT,
                        FindingCategory.PERMISSION,
                    ),
                )

            assertThat(comparison).isEqualTo(expected)
        }

    @Test fun `should detect a new service added in the target artifact`() =
        errorAwareTest {
            val serviceName = "io.dotanuki.app.MediaPlayerService"
            val missingService = AndroidComponent(serviceName, SERVICE)
            val withMissingService = referenceArtifact.androidComponents + missingService
            val newReference = referenceArtifact.copy(androidComponents = withMissingService)

            val comparison = comparator.compare(newReference, completeBaseline)

            val expected =
                setOf(
                    ComparisonFinding(serviceName, BrokenExpectation.MISSING_ON_BASELINE, FindingCategory.COMPONENT),
                )

            assertThat(comparison).isEqualTo(expected)
        }

    @Test fun `should detect an invalid activity tracked on the baseline`() =
        errorAwareTest {
            val activityName = "io.dotanuki.app.DeepLinkActivity"
            val dereferencedActivity = AndroidComponent(activityName, ACTIVITY)
            val withDereferencedActivity = referenceArtifact.androidComponents + dereferencedActivity
            val newReference = completeBaseline.copy(androidComponents = withDereferencedActivity)

            val comparison = comparator.compare(referenceArtifact, newReference)

            val expected =
                setOf(
                    ComparisonFinding(activityName, BrokenExpectation.MISSING_ON_ARTIFACT, FindingCategory.COMPONENT),
                )

            assertThat(comparison).isEqualTo(expected)
        }

    @Test fun `should detect multiple changes`() =
        errorAwareTest {
            val addedPermission = "android.permission.WRITE_EXTERNAL_STORAGE"
            val newPermissions = referenceArtifact.androidPermissions + addedPermission

            val removedActivity = "io.dotanuki.app.SplashActivity"
            val addedProvider = "com.squareup.picasso.PicassoProvider"
            val newComponents =
                referenceArtifact.androidComponents +
                    AndroidComponent(addedProvider, PROVIDER) -
                    AndroidComponent(removedActivity, ACTIVITY)

            val newReference =
                referenceArtifact.copy(
                    androidComponents = newComponents,
                    androidPermissions = newPermissions,
                )

            val comparison = comparator.compare(newReference, compactBaseline)

            val expected =
                setOf(
                    ComparisonFinding(
                        addedPermission,
                        BrokenExpectation.MISSING_ON_BASELINE,
                        FindingCategory.PERMISSION,
                    ),
                    ComparisonFinding(addedProvider, BrokenExpectation.MISSING_ON_BASELINE, FindingCategory.COMPONENT),
                )

            assertThat(comparison).isEqualTo(expected)
        }
}
