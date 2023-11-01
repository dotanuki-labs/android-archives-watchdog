/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.comparison

data class ComparisonFinding(
    val what: String,
    val expectation: BrokenExpectation,
    val category: FindingCategory
)
