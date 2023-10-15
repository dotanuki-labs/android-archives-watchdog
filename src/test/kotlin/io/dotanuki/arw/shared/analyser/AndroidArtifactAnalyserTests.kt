package io.dotanuki.arw.shared.analyser

import com.google.common.truth.Truth.assertThat
import io.dotanuki.arw.helpers.fixtureFromResources
import io.dotanuki.arw.helpers.raiseAware
import io.dotanuki.arw.overview.ReleasableOverview
import org.junit.Test

class AndroidArtifactAnalyserTests {

    @Test fun `should analyse a debug apk with success`() = raiseAware {
        val target = fixtureFromResources("app-debug.apk")
        val overview = AndroidArtifactAnalyser.overview(target)

        val expected = ReleasableOverview(
            applicationId = "io.dotanuki.norris.android.debug",
            debuggable = true,
            minSdk = 28,
            targetSdk = 33,
            totalPermissions = 5,
            dangerousPermissions = true
        )

        assertThat(overview).isEqualTo(expected)
    }

    @Test fun `should analyse a release apk with success`() = raiseAware {
        val target = fixtureFromResources("app-release.apk")
        val overview = AndroidArtifactAnalyser.overview(target)

        val expected = ReleasableOverview(
            applicationId = "io.dotanuki.norris.android",
            debuggable = false,
            minSdk = 28,
            targetSdk = 33,
            totalPermissions = 2,
            dangerousPermissions = false
        )

        assertThat(overview).isEqualTo(expected)
    }
}
