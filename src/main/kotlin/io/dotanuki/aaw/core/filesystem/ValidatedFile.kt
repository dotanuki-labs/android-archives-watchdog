/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.filesystem

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import io.dotanuki.aaw.core.errors.AawError
import java.io.File

object ValidatedFile {
    operator fun invoke(filePath: String): Either<AawError, String> =
        File(filePath)
            .run {
                if (!exists()) {
                    AawError("$filePath does not exist").left()
                }

                if (!isFile()) {
                    AawError("$filePath is not a file").left()
                }
            }.let {
                filePath.right()
            }
}
