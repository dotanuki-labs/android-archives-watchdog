package io.dotanuki.arw.overview

class ReleasableOverview(
    val locationPath: String,
    val type: Type,
    val minSdk: Int,
    val targetSdk: Int,
    val totalPermissions: Int,
    val sensitivePermissions: Boolean
) {

    enum class Type(val description: String) {
        AAB("App Bundle"),
        APK("App Archive")
    }
}
