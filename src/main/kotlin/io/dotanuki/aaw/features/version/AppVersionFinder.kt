/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.version

import arrow.core.raise.ensure
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.errors.ErrorAware
import io.dotanuki.aaw.core.logging.Logging
import java.util.*

object AppVersionFinder {

    context (ErrorAware, Logging)
    fun find(): AppVersion = try {
        val properties = Properties().apply {
            load(ClassLoader.getSystemClassLoader().getResourceAsStream("versions.properties"))
        }

        logger.debug("Successfully loaded version.properties file")

        val actualVersion = properties["latest"]
        ensure(actualVersion != null) { AawError("Cannot find 'latest' app version") }

        logger.debug("Latest version as per tracked in resource -> $actualVersion")
        AppVersion(actualVersion.toString())
    } catch (surfaced: Throwable) {
        raise(AawError("Failed when locating app resources", surfaced))
    }
}
