package io.dotanuki.aaw.features.comparison

import kotlinx.serialization.Serializable

@Serializable
data class SerializableComparison(
    val outcome: String,
    val results: List<ComparisonResult>
)

@Serializable
data class ComparisonResult(
    val item: String,
    val category: String,
    val finding: String
)