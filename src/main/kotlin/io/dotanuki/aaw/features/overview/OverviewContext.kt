/*
 * Copyright 2025 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.overview

import io.dotanuki.aaw.core.android.AndroidArtifactAnalyser
import kotlinx.serialization.json.Json

data class OverviewContext(
    val jsonSerializer: Json,
    val analyser: AndroidArtifactAnalyser,
)
