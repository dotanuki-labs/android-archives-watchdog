/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.baseline

import net.peanuuutz.tomlkt.Toml

data class BaselineContext(
    val tomlSerializer: Toml,
)
