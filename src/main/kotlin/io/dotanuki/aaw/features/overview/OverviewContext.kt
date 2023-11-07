/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.overview

import kotlinx.serialization.json.Json

data class OverviewContext(
    val jsonSerializer: Json,
)
