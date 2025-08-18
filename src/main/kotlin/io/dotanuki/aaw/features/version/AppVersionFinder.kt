/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.version

import arrow.core.Either
import arrow.core.left
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.logging.Logger
import java.util.Properties

class AppVersionFinder(
    private val logger: Logger,
) {
    fun find(): Either<AawError, AppVersion> =

        Either
            .catch {
                val properties =
                    Properties().apply {
                        load(ClassLoader.getSystemClassLoader().getResourceAsStream("versions.properties"))
                    }

                logger.debug("Successfully loaded version.properties file")
                val actualVersion =
                    properties["latest"] ?: return AawError("Cannot find 'latest' app version").left()

                logger.debug("Latest version as per tracked in resource -> $actualVersion")

                AppVersion(actualVersion.toString())
            }.mapLeft {
                AawError("Failed when locating app version", it)
            }
}
