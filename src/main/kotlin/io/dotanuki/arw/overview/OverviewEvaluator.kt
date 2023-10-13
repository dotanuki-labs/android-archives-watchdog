package io.dotanuki.arw.overview

class OverviewEvaluator {

    fun evaluate(): ReleasableOverview = ReleasableOverview(
        locationPath = "~/Users/lonewolf/Desktop/norris.apk",
        type = ReleasableOverview.Type.APK,
        minSdk = 28,
        targetSdk = 33,
        totalPermissions = 4,
        sensitivePermissions = false
    )
}
