/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.android

import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.errors.ErrorAware

data class SuppliedArtifact(
    val filePath: String,
    val type: AndroidArtifactType,
) {
    companion object {
        context (ErrorAware)
        fun from(filePath: String): SuppliedArtifact =
            when {
                filePath.endsWith(".aab") -> AndroidArtifactType.AAB
                filePath.endsWith(".apk") -> AndroidArtifactType.APK
                else -> raise(AawError("$filePath does not have .aab or .apk file extension"))
            }.let {
                SuppliedArtifact(filePath, it)
            }
    }
}
