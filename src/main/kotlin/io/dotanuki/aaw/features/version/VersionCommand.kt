/*
 * Copyright 2025 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.version

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import io.dotanuki.aaw.core.cli.ExitCodes
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.logging.Logger
import kotlin.system.exitProcess

class VersionCommand(
    private val logger: Logger,
    private val versionFinder: AppVersionFinder,
) : CliktCommand(
        name = "version",
    ) {
    override fun help(context: Context): String = "aaw version"

    override fun run() {
        versionFinder
            .find()
            .onLeft { reportFailure(it) }
            .onRight { printVersion(it) }
    }

    private fun printVersion(appVersion: AppVersion) {
        logger.info("aaw - v${appVersion.current}")
        logger.newLine()
    }

    private fun reportFailure(surfaced: AawError) {
        logger.error(surfaced)
        logger.newLine()
        exitProcess(ExitCodes.FAILURE)
    }
}
