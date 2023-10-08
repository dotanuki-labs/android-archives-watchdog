package io.dotanuki.arw

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MainTests {

    @Test fun `simple sum`() {
        val sum = 1 + 1
        assertThat(sum).isEqualTo(2)
    }
}
