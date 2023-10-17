package io.dotanuki.arw.core.infrastructure.android

import com.google.common.truth.Truth.assertThat
import io.dotanuki.arw.core.domain.models.AnalysedArtifact
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
            )
        )

        assertThat(analysed).isEqualTo(expected)
    }
}
