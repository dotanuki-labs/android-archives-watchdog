/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.android

import com.github.ajalt.mordant.terminal.Terminal
import com.google.common.truth.Truth.assertThat
import io.dotanuki.aaw.core.logging.Logger
import io.dotanuki.aaw.core.logging.Logging
import io.dotanuki.aaw.helpers.errorAwareTest
import io.dotanuki.aaw.helpers.fixtureFromResources
import org.junit.Test

class AndroidArtifactAnalyserTests {

    private val analyser by lazy {
        val terminal = Terminal()
        val logger = Logger(terminal, false)

        with(Logging(logger)) {
            AndroidArtifactAnalyser()
        }
    }

    private val developmentArtifact = AnalysedArtifact(
        applicationId = "io.dotanuki.norris.android.debug",
        minSdk = 28,
        targetSdk = 33,
        androidPermissions = setOf(
            "android.permission.INTERNET",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.POST_NOTIFICATIONS",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "io.dotanuki.norris.android.debug.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
        ),
        androidFeatures = setOf(
            "android.hardware.screen.portrait",
            "android.hardware.faketouch"
        ),
        androidComponents = setOf(
            AndroidComponent("io.dotanuki.app.SplashActivity", AndroidComponentType.ACTIVITY),
            AndroidComponent("io.dotanuki.features.facts.ui.FactsActivity", AndroidComponentType.ACTIVITY),
            AndroidComponent("io.dotanuki.features.search.ui.SearchActivity", AndroidComponentType.ACTIVITY),
            AndroidComponent("leakcanary.internal.activity.LeakActivity", AndroidComponentType.ACTIVITY),
            AndroidComponent("leakcanary.internal.activity.LeakLauncherActivity", AndroidComponentType.ACTIVITY),
            AndroidComponent("leakcanary.internal.RequestPermissionActivity", AndroidComponentType.ACTIVITY),
            AndroidComponent("io.dotanuki.app.NorrisApplication", AndroidComponentType.APPLICATION),
            AndroidComponent("androidx.startup.InitializationProvider", AndroidComponentType.PROVIDER),
            AndroidComponent("leakcanary.internal.LeakCanaryFileProvider", AndroidComponentType.PROVIDER),
            AndroidComponent("leakcanary.internal.MainProcessAppWatcherInstaller", AndroidComponentType.PROVIDER),
            AndroidComponent("leakcanary.internal.PlumberInstaller", AndroidComponentType.PROVIDER),
            AndroidComponent("leakcanary.internal.NotificationReceiver", AndroidComponentType.RECEIVER),
            AndroidComponent("androidx.profileinstaller.ProfileInstallReceiver", AndroidComponentType.RECEIVER)
        )
    )

    private val releaseArtifact = AnalysedArtifact(
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
            AndroidComponent("io.dotanuki.app.SplashActivity", AndroidComponentType.ACTIVITY),
            AndroidComponent("io.dotanuki.features.facts.ui.FactsActivity", AndroidComponentType.ACTIVITY),
            AndroidComponent("io.dotanuki.features.search.ui.SearchActivity", AndroidComponentType.ACTIVITY),
            AndroidComponent("io.dotanuki.app.NorrisApplication", AndroidComponentType.APPLICATION),
            AndroidComponent("androidx.startup.InitializationProvider", AndroidComponentType.PROVIDER),
            AndroidComponent("androidx.profileinstaller.ProfileInstallReceiver", AndroidComponentType.RECEIVER)
        )
    )

    @Test fun `should analyse a debug apk with success`() = errorAwareTest {
        val target = fixtureFromResources("app-debug.apk")
        val analysed = analyser.analyse(target)

        assertThat(analysed).isEqualTo(developmentArtifact)
    }

    @Test fun `should analyse a release apk with success`() = errorAwareTest {
        val target = fixtureFromResources("app-release.apk")
        val analysed = analyser.analyse(target)

        assertThat(analysed).isEqualTo(releaseArtifact)
    }

    @Test fun `should analyse a release app bundle with success`() = errorAwareTest {
        val target = fixtureFromResources("app-release.aab")
        val analysed = analyser.analyse(target)

        assertThat(analysed).isEqualTo(releaseArtifact)
    }
}
