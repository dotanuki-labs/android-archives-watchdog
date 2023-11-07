/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw

fun main(args: Array<String>) {
    with(Injection.inject(args)) {
        entrypoint.main(args)
    }
}
