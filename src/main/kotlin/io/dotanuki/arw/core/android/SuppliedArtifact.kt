package io.dotanuki.arw.core.android

import io.dotanuki.arw.core.errors.ArwError
import io.dotanuki.arw.core.errors.ErrorAware

data class SuppliedArtifact(
    val filePath: String,
    val type: AndroidArtifactType
) {
    companion object {

        context (ErrorAware)
        fun from(filePath: String): SuppliedArtifact =
            when {
                filePath.endsWith(".aab") -> AndroidArtifactType.AAB
                filePath.endsWith(".apk") -> AndroidArtifactType.APK
                else -> raise(ArwError("$filePath does not have .aab or .apk file extension"))
            }.let {
                SuppliedArtifact(filePath, it)
            }
    }
}