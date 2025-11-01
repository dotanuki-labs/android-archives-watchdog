/*
 * Copyright 2025 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.comparison

import io.dotanuki.aaw.core.android.AndroidArtifactAnalyser
import kotlinx.serialization.json.Json
import net.peanuuutz.tomlkt.Toml

data class CompareContext(
    val tomlSerializer: Toml,
    val jsonSerializer: Json,
    val analyser: AndroidArtifactAnalyser,
    val comparator: ArtifactsComparator,
)
