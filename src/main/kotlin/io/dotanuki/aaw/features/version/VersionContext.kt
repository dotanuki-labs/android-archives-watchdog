/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.features.version

import io.dotanuki.aaw.core.logging.Logger
import io.dotanuki.aaw.core.logging.LoggingContext

data class VersionContext(
    override val logger: Logger
) : LoggingContext
