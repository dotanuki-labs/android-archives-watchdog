/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

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
import io.dotanuki.aaw.core.logging.Logging
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

context (Logging)
class AndroidArtifactAnalyser {

    private val sdkBridge by lazy {
        AndroidSDKBridge()
    }

    context (ErrorAware)
    fun analyse(pathToTarget: String): AnalysedArtifact {
        val artifact = SuppliedArtifact.from(pathToTarget)

        logger.debug("Successfully identified artifact type -> ${artifact.type.name}")

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
        logger.debug("Starting analysis -> ${apk.filePath}")
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

            logger.debug("Decoding AndroidManifest.xml binary file")
            val decodedXml = BinaryXmlParser.decodeXml(manifestPath.absolutePathString(), bytesToDecode)

            val inputStream = ByteArrayInputStream(decodedXml)
            AndroidManifestParser.parse(inputStream).also {
                logger.debug("Successfully parsed AndroidManifest.xml")
            }
        } catch (surfaced: Throwable) {
            raise(AawError("Failed when reading AndroidManifest", surfaced))
        }

    context (ErrorAware)
    private fun retrieveAppInfoWithAapt(pathToArtifact: String) =
        try {
            val sdkHandler = AndroidSdkHandler.getInstance(
                AndroidLocationsSingleton,
                sdkBridge.sdkFolder.asPath()
            )

            val aaptInvoker = AaptInvoker(sdkHandler, NullLogger())

            logger.debug("Dumping application info using aapt")
            AndroidApplicationInfo
                .parseBadging(aaptInvoker.dumpBadging(pathToArtifact.asFile()))
                .also { logger.debug("Successfully extracted application info") }
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
        logger.debug("Evaluating AppBundle information")
        val artifactName = artifact.filePath.split("/").last().replace(".aab", "")
        val tempDir = Files.createTempDirectory("arw-$artifactName-extraction").toFile()
        val apkContainerOutput = "$tempDir/$artifactName.apks"

        logger.debug("Retrieving fake keystore to sign artifacts")
        val keystore = ClassLoader.getSystemClassLoader().getResourceAsStream("aaw.keystore")?.readAllBytes()
        ensure(keystore != null) { AawError("Failed when reading aaw.keystore") }
        val keystoreFile = File("$tempDir/aaw.keystore").apply { writeBytes(keystore) }

        logger.debug("Generating universal APK from AppBundle with Bundletool")
        val flags = arrayOf(
            "--bundle=${artifact.filePath}",
            "--output=$apkContainerOutput",
            "--aapt2=${locateAapt2FromSdk()}",
            "--ks=$keystoreFile",
            "--ks-pass=pass:aaw-pass",
            "--ks-key-alias=aaw-alias",
            "--key-pass=pass:aaw-pass",
            "--mode=universal"
        )

        val parsedFlags = FlagParser().parse(*flags)
        val command = BuildApksCommand.fromFlags(parsedFlags, null)
        val apkContainerPath = command.execute()

        val destinationFolder = "$tempDir/extracted"
        Unzipper.unzip(apkContainerPath.toFile(), "$tempDir/extracted")

        "$destinationFolder/universal.apk".also {
            logger.debug("Successfully extracted universal APK from -> ${artifact.filePath}")
        }
    } catch (surfaced: Throwable) {
        raise(AawError("Cannot convert universal APK from AppBundle", surfaced))
    }

    context (ErrorAware)
    private fun locateAapt2FromSdk(): File {
        logger.debug("Locating aapt2 using Android SDK installation")
        val sdkHandler = AndroidSdkHandler.getInstance(
            AndroidLocationsSingleton,
            sdkBridge.sdkFolder.asPath()
        )

        val buildTools = sdkHandler.getLatestBuildTool(LoggerProgressIndicatorWrapper(NullLogger()), true)
        ensure(buildTools != null) {
            AawError("Failed to locate build tools inside your Android SDK installation")
        }

        return buildTools.location.resolve(SdkConstants.FN_AAPT2).toFile().also {
            logger.debug("Found aapt2 -> $it")
        }
    }

    private fun String.asPath() = Paths.get(this)

    private fun String.asFile() = File(this)
}
