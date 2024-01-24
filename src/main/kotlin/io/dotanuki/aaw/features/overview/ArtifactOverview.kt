/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.overview

import kotlinx.serialization.Serializable

@Serializable
data class ArtifactOverview(
    val applicationId: String,
    val minSdk: Int,
    val targetSdk: Int,
    val totalUsedFeatures: Int,
    val totalPermissions: Int,
    val dangerousPermissions: Boolean,
    val totalActivities: Int,
    val totalServices: Int,
    val totalReceivers: Int,
    val totalProviders: Int,
)
