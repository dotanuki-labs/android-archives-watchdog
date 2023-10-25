package io.dotanuki.aaw.features.baseline

import io.dotanuki.aaw.core.errors.ErrorAware

object ValidatedPackages {

    context (ErrorAware)
    operator fun invoke(packages: String?): List<String> =
        packages?.split(",") ?: emptyList()
}
