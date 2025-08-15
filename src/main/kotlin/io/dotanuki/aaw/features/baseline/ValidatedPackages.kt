/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.baseline

import arrow.core.Either
import io.dotanuki.aaw.core.errors.AawError

object ValidatedPackages {
    operator fun invoke(packages: String?): Either<AawError, List<String>> =
        Either
            .catch {
                packages?.split(",") ?: emptyList()
            }.mapLeft {
                AawError("Failed to evaluate trusted packages", it)
            }
}
