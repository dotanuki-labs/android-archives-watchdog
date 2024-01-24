/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.baseline

import io.dotanuki.aaw.core.android.AndroidArtifactAnalyser
import net.peanuuutz.tomlkt.Toml

data class BaselineContext(
    val tomlSerializer: Toml,
    val analyser: AndroidArtifactAnalyser,
)
