package io.dotanuki.aaw.core.errors

import arrow.core.raise.Raise

typealias ErrorAware = Raise<AawError>

data class AawError(val description: String, val wrapped: Throwable? = null)
