/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw

fun main(args: Array<String>) {
    with(Injection) {
        entrypoint.main(args)
    }
}
