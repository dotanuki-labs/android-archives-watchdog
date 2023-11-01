/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.android

import arrow.core.raise.ensure
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.errors.ErrorAware

object AndroidSDKBridge {

    context (ErrorAware)
    fun sdkFolder(): String =
        with(System.getenv()) {
            when {
                containsKey(FIRST_OPTION) -> this[FIRST_OPTION]
                containsKey(SECOND_OPTION) -> this[SECOND_OPTION]
                containsKey(THIRD_OPTION) -> this[THIRD_OPTION]
                else -> null
            }.let { androidHome ->
                ensure(androidHome != null) { AawError(CANNOT_LOCATE_ANDROID_SDK) }
                androidHome
            }
        }

    private const val FIRST_OPTION = "ANDROID_HOME"
    private const val SECOND_OPTION = "ANDROID_SDK_HOME"
    private const val THIRD_OPTION = "ANDROID_SDK"

    private val CANNOT_LOCATE_ANDROID_SDK =
        """
        Could not locate your Android SDK installation folder. 
        Ensure that have it exposed in one of the following environment variables
        • $FIRST_OPTION
        • $SECOND_OPTION
        • $THIRD_OPTION
        """.trimIndent()
}
