package io.dotanuki.arw.helpers

import arrow.core.raise.recover
import io.dotanuki.arw.shared.errors.ErrorAware

fun errorAwareTest(assertion: ErrorAware.() -> Unit) =
    recover(
        block = assertion,
        recover = {
            throw AssertionError("Should not recover on this test!\nError = $it")
        }
    )