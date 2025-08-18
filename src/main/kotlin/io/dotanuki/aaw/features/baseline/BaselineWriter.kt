/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.baseline

import com.github.ajalt.mordant.rendering.TextColors.cyan
import io.dotanuki.aaw.core.logging.Logger
import io.dotanuki.aaw.core.toml.WatchdogConfig
import net.peanuuutz.tomlkt.Toml
import java.nio.file.Paths
import kotlin.io.path.writeText

class BaselineWriter(
    private val logger: Logger,
    private val tomlSerializer: Toml,
) {
    fun write(
        baseline: WatchdogConfig,
        filename: String,
    ) {
        val toml = tomlSerializer.encodeToString(WatchdogConfig.serializer(), baseline)

        Paths.get(filename).run {
            writeText(toml)
            logger.newLine()
            logger.info("Baseline available at : ${cyan(toFile().toString())}")
        }
    }
}
