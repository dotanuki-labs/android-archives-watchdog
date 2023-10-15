package io.dotanuki.arw.shared.errors

data class ArwError(val description: String, val wrapped: Throwable? = null)
