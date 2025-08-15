/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.toml

import arrow.core.Either
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.features.comparison.CompareContext
import java.io.File

object ValidatedTOML {
    context (CompareContext)
    operator fun invoke(baselinePath: String): Either<AawError, WatchdogConfig> =
        Either
            .catch {
                tomlSerializer.decodeFromString(WatchdogConfig.serializer(), File(baselinePath).readText())
            }.mapLeft {
                AawError("Failed when parsing configuration", it)
            }
}
