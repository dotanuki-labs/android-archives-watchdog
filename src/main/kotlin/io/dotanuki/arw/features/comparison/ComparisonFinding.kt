package io.dotanuki.arw.features.comparison

data class ComparisonFinding(
    val what: String,
    val expectation: BrokenExpectation,
    val category: FindingCategory
)
