package io.dotanuki.arw.shared.analyser

import com.google.common.truth.Truth.assertThat
import io.dotanuki.arw.helpers.fixtureFromResources
import io.dotanuki.arw.overview.ReleasableOverview
import org.junit.Test

class AndroidArtifactAnalyserTests {

    @Test fun `should analyse a debug apk with success`() {
        val target = fixtureFromResources("app-debug.apk")
        val analyser = AndroidArtifactAnalyser()
        val overview = analyser.overview(target)

        val expected = ReleasableOverview(
            applicationId = "io.dotanuki.norris.android.debug",
            debuggable = true,
            minSdk = 28,
            targetSdk = 33,
            totalPermissions = 5,
            sensitivePermissions = false
        )

        assertThat(overview).isEqualTo(expected)
    }
}
