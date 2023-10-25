package io.dotanuki.aaw.core.android

import arrow.core.raise.ensure
import com.android.SdkConstants
import com.android.ide.common.xml.AndroidManifestParser
import com.android.ide.common.xml.ManifestData
import com.android.prefs.AndroidLocationsSingleton
import com.android.sdklib.repository.AndroidSdkHandler
import com.android.sdklib.repository.LoggerProgressIndicatorWrapper
import com.android.tools.apk.analyzer.AaptInvoker
import com.android.tools.apk.analyzer.AndroidApplicationInfo
import com.android.tools.apk.analyzer.Archives
import com.android.tools.apk.analyzer.BinaryXmlParser
import com.android.tools.build.bundletool.commands.BuildApksCommand
import com.android.tools.build.bundletool.flags.FlagParser
import com.android.utils.NullLogger
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.errors.ErrorAware
import io.dotanuki.aaw.core.filesystem.Unzipper
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

object AndroidArtifactAnalyser {

    context (ErrorAware)
    fun analyse(pathToTarget: String): AnalysedArtifact {
        val artifact = SuppliedArtifact.from(pathToTarget)

        return when (artifact.type) {
            AndroidArtifactType.APK -> analyseApk(artifact)
            AndroidArtifactType.AAB -> analyseAab(artifact)
        }
    }

    context (ErrorAware)
    private fun analyseAab(aab: SuppliedArtifact): AnalysedArtifact =
        analyseApk(
            SuppliedArtifact(extractUniversalApkFromBundle(aab), AndroidArtifactType.APK)
        )

    context (ErrorAware)
    private fun analyseApk(apk: SuppliedArtifact): AnalysedArtifact {
        val appInfo = retrieveAppInfoWithAapt(apk.filePath)
        val parsedManifest = parseAndroidManifestFromApk(apk.filePath)

        return AnalysedArtifact(
            applicationId = appInfo.packageId,
            androidPermissions = appInfo.permissions.toSortedSet(),
            androidFeatures = appInfo.usesFeature.keys.toSortedSet(),
            androidComponents = parsedManifest.extractComponents(),
            minSdk = parsedManifest.minSdkVersion,
            targetSdk = parsedManifest.targetSdkVersion
        )
    }

    context (ErrorAware)
    private fun parseAndroidManifestFromApk(pathToArtifact: String) =
        try {
            val archiveContext = Archives.open(pathToArtifact.asPath())
            val manifestPath = archiveContext.archive.contentRoot.resolve("AndroidManifest.xml")
            val bytesToDecode = Files.readAllBytes(manifestPath)
            val decodedXml = BinaryXmlParser.decodeXml(manifestPath.absolutePathString(), bytesToDecode)
            val inputStream = ByteArrayInputStream(decodedXml)

            AndroidManifestParser.parse(inputStream)
        } catch (surfaced: Throwable) {
            raise(AawError("Failed when reading AndroidManifest", surfaced))
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
            raise(AawError("Failed when invoking aapt from Android SDK", surfaced))
        }

    private fun ManifestData.extractComponents(): Set<AndroidComponent> =
        keepClasses
            .sortedBy { it.type }
            .filter { it.type.uppercase() in AndroidComponentType.entries.map { it.name } }
            .map {
                AndroidComponent(
                    it.name,
                    AndroidComponentType.valueOf(it.type.uppercase())
                )
            }
            .toSet()

    context (ErrorAware)
    private fun extractUniversalApkFromBundle(artifact: SuppliedArtifact): String = try {
        val artifactName = artifact.filePath.split("/").last().replace(".aab", "")
        val tempDir = Files.createTempDirectory("arw-$artifactName-extraction").toFile()
        val apkContainerOutput = "$tempDir/$artifactName.apks"

        val flags = arrayOf(
            "--bundle=${artifact.filePath}",
            "--output=$apkContainerOutput",
            "--aapt2=${locateAapt2FromSdk()}",
            "--mode=universal"
        )

        val parsedFlags = FlagParser().parse(*flags)
        val command = BuildApksCommand.fromFlags(parsedFlags, null)
        val apkContainerPath = command.execute()

        val destinationFolder = "$tempDir/extracted"
        Unzipper.unzip(apkContainerPath.toFile(), "$tempDir/extracted")

        "$destinationFolder/universal.apk"
    } catch (surfaced: Throwable) {
        raise(AawError("Cannot convert universal APK from AppBundle", surfaced))
    }

    context (ErrorAware)
    private fun locateAapt2FromSdk(): File {
        val sdkHandler = AndroidSdkHandler.getInstance(
            AndroidLocationsSingleton,
            AndroidSDKBridge.sdkFolder().asPath()
        )

        val buildTools = sdkHandler.getLatestBuildTool(LoggerProgressIndicatorWrapper(NullLogger()), true)
        ensure(buildTools != null) {
            AawError("Failed to locate build tools inside your Android SDK installation")
        }

        return buildTools.location.resolve(SdkConstants.FN_AAPT2).toFile()
    }

    private fun String.asPath() = Paths.get(this)

    private fun String.asFile() = File(this)
}
