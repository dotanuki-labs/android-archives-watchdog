package io.dotanuki.arw.shared.analyser

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import io.dotanuki.arw.shared.errors.ArwError

object AndroidSDKBridge {

    context (Raise<ArwError>)
    fun sdkFolder(): String =
        with(System.getenv()) {
            when {
                containsKey(FIRST_OPTION) -> this[FIRST_OPTION]
                containsKey(SECOND_OPTION) -> this[SECOND_OPTION]
                containsKey(THIRD_OPTION) -> this[THIRD_OPTION]
                else -> null
            }.let { androidHome ->
                ensure(androidHome != null) { ArwError(CANNOT_LOCATE_ANDROID_SDK) }
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
