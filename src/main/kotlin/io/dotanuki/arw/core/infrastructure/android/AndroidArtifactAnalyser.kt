package io.dotanuki.arw.core.infrastructure.android

import com.android.ide.common.xml.AndroidManifestParser
import com.android.ide.common.xml.ManifestData
import com.android.prefs.AndroidLocationsSingleton
import com.android.sdklib.repository.AndroidSdkHandler
import com.android.tools.apk.analyzer.AaptInvoker
import com.android.tools.apk.analyzer.AndroidApplicationInfo
import com.android.tools.apk.analyzer.Archives
import com.android.tools.apk.analyzer.BinaryXmlParser
import com.android.utils.NullLogger
import io.dotanuki.arw.core.domain.errors.ArwError
import io.dotanuki.arw.core.domain.errors.ErrorAware
import io.dotanuki.arw.core.domain.models.AnalysedArtifact
import io.dotanuki.arw.core.domain.models.AndroidComponent
import io.dotanuki.arw.core.domain.models.AndroidComponentType
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

object AndroidArtifactAnalyser {

    context (ErrorAware)
    fun analyse(pathToTarget: String): AnalysedArtifact {
        val appInfo = retrieveAppInfoWithAapt(pathToTarget)
        val parsedManifest = retrieveParsedAndroidManifest(pathToTarget)

        return AnalysedArtifact(
            applicationId = appInfo.packageId,
            androidPermissions = appInfo.permissions.apply { sorted() },
            androidFeatures = appInfo.usesFeature.keys.apply { sorted() },
            androidComponents = parsedManifest.extractComponents(),
            minSdk = parsedManifest.minSdkVersion,
            targetSdk = parsedManifest.targetSdkVersion
        )
    }

    context (ErrorAware)
    private fun retrieveParsedAndroidManifest(pathToArtifact: String) =
        try {
            val archiveContext = Archives.open(pathToArtifact.asPath())
            val manifestPath = archiveContext.archive.contentRoot.resolve("AndroidManifest.xml")
            val bytesToDecode = Files.readAllBytes(manifestPath)
            val decodedXml = BinaryXmlParser.decodeXml(manifestPath.absolutePathString(), bytesToDecode)
            val inputStream = ByteArrayInputStream(decodedXml)

            AndroidManifestParser.parse(inputStream)
        } catch (surfaced: Throwable) {
            raise(ArwError("Failed when reading AndroidManifest", surfaced))
        }

    context (ErrorAware)
    private fun retrieveAppInfoWithAapt(pathToArtifact: String) =
        try {
            val sdkHandler = AndroidSdkHandler.getInstance(
                AndroidLocationsSingleton,
                AndroidSDKBridge.sdkFolder().asPath()
            )

            val aaptInvoker = AaptInvoker(sdkHandler, NullLogger())

            AndroidApplicationInfo.parseBadging(aaptInvoker.dumpBadging(pathToArtifact.asFile()))
        } catch (surfaced: Throwable) {
            raise(ArwError("Failed when invoking aapt from Android SDK", surfaced))
        }

    private fun ManifestData.extractComponents(): Set<AndroidComponent> =
        keepClasses
            .sortedBy { it.type }
            .map {
                AndroidComponent(
                    it.name,
                    AndroidComponentType.valueOf(it.type.uppercase())
                )
            }
            .toSet()

    private fun String.asPath() = Paths.get(this)

    private fun String.asFile() = File(this)
}
