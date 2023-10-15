package io.dotanuki.arw.helpers

import arrow.core.raise.Raise
import arrow.core.raise.recover

fun <T> raiseAware(test: Raise<T>.() -> Unit) =
    recover(
        block = test,
        recover = {
            throw AssertionError("Should not recover on this test! -> Error = $it")
        }
    )
