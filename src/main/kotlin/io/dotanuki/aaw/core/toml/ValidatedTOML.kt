/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.toml

import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.errors.ErrorAware
import io.dotanuki.aaw.features.comparison.CompareContext
import java.io.File

object ValidatedTOML {

    context (ErrorAware, CompareContext)
    operator fun invoke(baselinePath: String): WatchdogConfig =
        try {
            tomlSerializer.decodeFromString(WatchdogConfig.serializer(), File(baselinePath).readText())
        } catch (surfaced: Throwable) {
            raise(AawError("Failed when parsing configuration", surfaced))
        }
}
