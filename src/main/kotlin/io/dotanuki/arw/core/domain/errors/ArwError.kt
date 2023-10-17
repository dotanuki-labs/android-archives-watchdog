package io.dotanuki.arw.core.domain.errors

import arrow.core.raise.Raise

typealias ErrorAware = Raise<ArwError>

data class ArwError(val description: String, val wrapped: Throwable? = null)
