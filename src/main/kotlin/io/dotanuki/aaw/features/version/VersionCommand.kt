/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.version

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import io.dotanuki.aaw.core.cli.ExitCodes
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.errors.ErrorAware
import kotlin.system.exitProcess

context (VersionContext)
class VersionCommand : CliktCommand(
    help = "aaw version",
    name = "version"
) {

    override fun run() {
        recover(::printVersion, ::reportFailure)
    }

    context (ErrorAware)
    private fun printVersion() {
        val appVersion = AppVersionFinder.find()
        logger.info("aaw - v${appVersion.current}")
    }

    private fun reportFailure(surfaced: AawError) {
        logger.error(surfaced)
        exitProcess(ExitCodes.FAILURE)
    }
}
