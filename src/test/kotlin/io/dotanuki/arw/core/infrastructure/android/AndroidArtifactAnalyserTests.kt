package io.dotanuki.arw.core.infrastructure.android

import com.google.common.truth.Truth.assertThat
import io.dotanuki.arw.core.domain.models.AnalysedArtifact
import io.dotanuki.arw.core.domain.models.AndroidComponent
import io.dotanuki.arw.core.domain.models.AndroidComponentType.ACTIVITY
import io.dotanuki.arw.core.domain.models.AndroidComponentType.APPLICATION
import io.dotanuki.arw.core.domain.models.AndroidComponentType.PROVIDER
import io.dotanuki.arw.core.domain.models.AndroidComponentType.RECEIVER
import io.dotanuki.arw.helpers.errorAwareTest
import io.dotanuki.arw.helpers.fixtureFromResources
import org.junit.Test

class AndroidArtifactAnalyserTests {

    @Test fun `should analyse a debug apk with success`() = errorAwareTest {
        val target = fixtureFromResources("app-debug.apk")
        val analysed = AndroidArtifactAnalyser.overview(target)

        val expected = AnalysedArtifact(
            applicationId = "io.dotanuki.norris.android.debug",
            debuggable = true,
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
                AndroidComponent("io.dotanuki.app.SplashActivity", ACTIVITY),
                AndroidComponent("io.dotanuki.features.facts.ui.FactsActivity", ACTIVITY),
                AndroidComponent("io.dotanuki.features.search.ui.SearchActivity", ACTIVITY),
                AndroidComponent("leakcanary.internal.activity.LeakActivity", ACTIVITY),
                AndroidComponent("leakcanary.internal.activity.LeakLauncherActivity", ACTIVITY),
                AndroidComponent("leakcanary.internal.RequestPermissionActivity", ACTIVITY),
                AndroidComponent("io.dotanuki.app.NorrisApplication", APPLICATION),
                AndroidComponent("androidx.startup.InitializationProvider", PROVIDER),
                AndroidComponent("leakcanary.internal.LeakCanaryFileProvider", PROVIDER),
                AndroidComponent("leakcanary.internal.MainProcessAppWatcherInstaller", PROVIDER),
                AndroidComponent("leakcanary.internal.PlumberInstaller", PROVIDER),
                AndroidComponent("leakcanary.internal.NotificationReceiver", RECEIVER),
                AndroidComponent("androidx.profileinstaller.ProfileInstallReceiver", RECEIVER)
            )
        )

        assertThat(analysed).isEqualTo(expected)
    }

    @Test fun `should analyse a release apk with success`() = errorAwareTest {
        val target = fixtureFromResources("app-release.apk")
        val analysed = AndroidArtifactAnalyser.overview(target)

        val expected = AnalysedArtifact(
            applicationId = "io.dotanuki.norris.android",
            debuggable = false,
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

        assertThat(analysed).isEqualTo(expected)
    }
}
