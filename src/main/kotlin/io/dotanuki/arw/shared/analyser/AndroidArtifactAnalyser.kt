package io.dotanuki.arw.shared.analyser

import arrow.core.raise.Raise
import com.android.ide.common.xml.AndroidManifestParser
import com.android.prefs.AndroidLocationsSingleton
import com.android.sdklib.repository.AndroidSdkHandler
import com.android.tools.apk.analyzer.AaptInvoker
import com.android.tools.apk.analyzer.AndroidApplicationInfo
import com.android.tools.apk.analyzer.Archives
import com.android.tools.apk.analyzer.BinaryXmlParser
import com.android.utils.NullLogger
import io.dotanuki.arw.overview.ReleasableOverview
import io.dotanuki.arw.shared.errors.ArwError
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

object AndroidArtifactAnalyser {

    context (Raise<ArwError>)
    fun overview(pathToTarget: String): ReleasableOverview {
        val appInfo = retrieveAppInfoWithAapt(pathToTarget)
        val parsedManifest = retrieveParsedAndroidManifest(pathToTarget)

        return ReleasableOverview(
            applicationId = appInfo.packageId,
            totalPermissions = appInfo.permissions.size,
            dangerousPermissions = AndroidPermissions.hasDangerous(appInfo.permissions),
            minSdk = parsedManifest.minSdkVersion,
            targetSdk = parsedManifest.targetSdkVersion,
            debuggable = parsedManifest.debuggable
        )
    }

    context (Raise<ArwError>)
    private fun retrieveParsedAndroidManifest(pathToArtifact: String) =
        try {
            val archiveContext = Archives.open(pathToArtifact.asPath())
            val manifestPath = archiveContext.archive.contentRoot.resolve("AndroidManifest.xml")
            val bytesToDecode = Files.readAllBytes(manifestPath)
            val decodedXml = BinaryXmlParser.decodeXml(manifestPath.absolutePathString(), bytesToDecode)
            val inputStream = ByteArrayInputStream(decodedXml)

            AndroidManifestParser.parse(inputStream)
        } catch (incoming: Throwable) {
            raise(ArwError("Failed when reading AndroidManifest", incoming))
        }

    context (Raise<ArwError>)
    private fun retrieveAppInfoWithAapt(pathToArtifact: String) =
        try {
            val sdkHandler = AndroidSdkHandler.getInstance(
                AndroidLocationsSingleton,
                AndroidSDKBridge.sdkFolder().asPath()
            )

            val aaptInvoker = AaptInvoker(sdkHandler, NullLogger())

            AndroidApplicationInfo.parseBadging(aaptInvoker.dumpBadging(pathToArtifact.asFile()))
        } catch (incoming: Throwable) {
            raise(ArwError("Failed when invoking aapt", incoming))
        }

    private fun String.asPath() = Paths.get(this)
    private fun String.asFile() = File(this)
}
