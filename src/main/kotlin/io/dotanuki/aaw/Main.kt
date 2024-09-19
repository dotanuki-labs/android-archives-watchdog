/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw

import com.github.ajalt.clikt.core.main

fun main(args: Array<String>) {
    val (verboseMode, filteredArguments) =
        when {
            !args.contains("--verbose") -> Pair(false, args)
            else -> Pair(true, args.toMutableList().apply { remove("--verbose") }.toTypedArray())
        }

    with(Injection(verboseMode)) {
        entrypoint.main(filteredArguments)
    }
}
