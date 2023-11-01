/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.comparison

import io.dotanuki.aaw.core.android.AndroidComponent

data class ArtifactBaseline(
    val applicationId: String,
    val androidPermissions: Set<String>,
    val androidFeatures: Set<String>,
    val androidComponents: Set<AndroidComponent>,
    val trustedPackages: Set<String> = emptySet()
)
