/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.baseline

import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.errors.ErrorAware

object ValidatedPackages {
    context (ErrorAware)
    operator fun invoke(packages: String?): List<String> =
        try {
            packages?.split(",") ?: emptyList()
        } catch (surfaced: Throwable) {
            raise(AawError("Failed to evaluate trusted packages", surfaced))
        }
}
