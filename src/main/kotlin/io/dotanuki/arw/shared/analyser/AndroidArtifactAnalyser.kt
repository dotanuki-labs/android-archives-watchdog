package io.dotanuki.arw.shared.analyser

import com.android.ide.common.xml.AndroidManifestParser
import com.android.prefs.AndroidLocationsSingleton
import com.android.sdklib.repository.AndroidSdkHandler
import com.android.tools.apk.analyzer.AaptInvoker
import com.android.tools.apk.analyzer.AndroidApplicationInfo
import com.android.tools.apk.analyzer.Archives
import com.android.tools.apk.analyzer.BinaryXmlParser
import com.android.utils.NullLogger
import io.dotanuki.arw.overview.ReleasableOverview
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class AndroidArtifactAnalyser {

    private val sdkBridge by lazy { AndroidSDKBridge() }

    private val aaptInvoker by lazy {
        val sdkHandler = AndroidSdkHandler.getInstance(
            AndroidLocationsSingleton,
            Paths.get(sdkBridge.sdkFolder)
        )

        AaptInvoker(sdkHandler, NullLogger())
    }

    fun overview(pathToTarget: String): ReleasableOverview {
        val appInfo = AndroidApplicationInfo.parseBadging(
            aaptInvoker.dumpBadging(File(pathToTarget))
        )

        val archiveContext = Archives.open(Paths.get(pathToTarget))
        val manifestPath = archiveContext.archive.contentRoot.resolve("AndroidManifest.xml")
        val bytesToDecoded = Files.readAllBytes(manifestPath)
        val decodedXml = BinaryXmlParser.decodeXml(manifestPath.absolutePathString(), bytesToDecoded)

        val inputStream = ByteArrayInputStream(decodedXml)
        val parsedManifest = AndroidManifestParser.parse(inputStream)

        return ReleasableOverview(
            applicationId = appInfo.packageId,
            totalPermissions = appInfo.permissions.size,
            minSdk = parsedManifest.minSdkVersion,
            targetSdk = parsedManifest.targetSdkVersion,
            sensitivePermissions = false, // TODO : evaluate sensitive permissions
            debuggable = parsedManifest.debuggable
        )
    }
}
