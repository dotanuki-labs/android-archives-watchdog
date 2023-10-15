package io.dotanuki.arw.shared.analyser

import arrow.core.raise.recover
import com.google.common.truth.Truth.assertThat
import io.dotanuki.arw.helpers.fixtureFromResources
import io.dotanuki.arw.overview.ReleasableOverview
import org.junit.Test

class AndroidArtifactAnalyserTests {

    @Test fun `should analyse a debug apk with success`() {
        recover(
            block = {
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
            },
            recover = {
                throw AssertionError("Should not recover on this test! -> Error = $it")
            }
        )
    }
}
