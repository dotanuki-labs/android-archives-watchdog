package io.dotanuki.arw.features.baseline

import io.dotanuki.arw.core.errors.ErrorAware

object ValidatedPackages {

    context (ErrorAware)
    operator fun invoke(packages: String?): List<String> =
        packages?.split(",") ?: emptyList()
}
