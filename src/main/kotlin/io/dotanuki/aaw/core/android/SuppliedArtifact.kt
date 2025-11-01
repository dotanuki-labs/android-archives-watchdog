/*
 * Copyright 2025 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.android

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.dotanuki.aaw.core.errors.AawError

data class SuppliedArtifact(
    val filePath: String,
    val type: AndroidArtifactType,
) {
    companion object {
        fun from(filePath: String): Either<AawError, SuppliedArtifact> =
            when {
                filePath.endsWith(".aab") -> AndroidArtifactType.AAB
                filePath.endsWith(".apk") -> AndroidArtifactType.APK
                else -> null
            }?.let {
                SuppliedArtifact(filePath, it).right()
            } ?: AawError("$filePath does not have .aab or .apk file extension").left()
    }
}
