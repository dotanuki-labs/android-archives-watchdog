/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.comparison

import kotlinx.serialization.json.Json
import net.peanuuutz.tomlkt.Toml

data class CompareContext(
    val tomlSerializer: Toml,
    val jsonSerializer: Json,
)
