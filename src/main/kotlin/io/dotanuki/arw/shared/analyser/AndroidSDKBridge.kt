package io.dotanuki.arw.shared.analyser

class AndroidSDKBridge {

    val sdkFolder by lazy {
        val env = System.getenv()

        when {
            env.containsKey("ANDROID_HOME") -> env["ANDROID_HOME"]
            env.containsKey("ANDROID_SDK_HOME") -> env["ANDROID_SDK_HOME"]
            env.containsKey("ANDROID_SDK") -> env["ANDROID_SDK"]
            else -> error("Cannot locate Android SDK")
        }.let { requireNotNull(it) }
    }
}
