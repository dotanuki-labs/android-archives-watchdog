package io.dotanuki.arw.features.comparison

import io.dotanuki.arw.core.domain.errors.ArwError
import io.dotanuki.arw.core.domain.errors.ErrorAware
import io.dotanuki.arw.features.common.ArtifactBaseline
import java.io.File

object ValidatedBaseline {

    context (ErrorAware, CompareContext)
    operator fun invoke(baselinePath: String): ArtifactBaseline = try {
        tomlSerializer.decodeFromString(ArtifactBaseline.serializer(), File(baselinePath).readText())
    } catch (surfaced: Throwable) {
        raise(ArwError("Failed when parsing configuration", surfaced))
    }
}
