package io.dotanuki.arw.core.toml

import io.dotanuki.arw.core.errors.ArwError
import io.dotanuki.arw.core.errors.ErrorAware
import io.dotanuki.arw.features.comparison.CompareContext
import java.io.File

object ValidatedTOML {

    context (ErrorAware, CompareContext)
    operator fun invoke(baselinePath: String): WatchdogConfig =
        try {
            tomlSerializer.decodeFromString(WatchdogConfig.serializer(), File(baselinePath).readText())
        } catch (surfaced: Throwable) {
            raise(ArwError("Failed when parsing configuration", surfaced))
        }
}
