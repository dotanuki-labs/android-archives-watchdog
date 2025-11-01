/*
 * Copyright 2025 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.filesystem

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.dotanuki.aaw.core.errors.AawError
import java.io.File

@Suppress("ReturnCount")
object ValidatedFile {
    operator fun invoke(filePath: String): Either<AawError, String> {
        val target = File(filePath)
        if (!target.exists()) {
            return AawError("$filePath does not exist").left()
        }

        if (!target.isFile()) {
            return AawError("$filePath is not a file").left()
        }

        return filePath.right()
    }
}
