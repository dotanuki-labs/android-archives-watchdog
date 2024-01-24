/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.helpers

import arrow.core.raise.recover
import io.dotanuki.aaw.core.errors.ErrorAware

fun errorAwareTest(assertion: ErrorAware.() -> Unit) =
    recover(
        block = assertion,
        recover = {
            throw AssertionError("Should not recover on this test!\nError = $it")
        },
    )
