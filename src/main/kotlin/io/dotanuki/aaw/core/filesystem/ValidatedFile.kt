/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.filesystem

import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.errors.ErrorAware
import java.io.File

object ValidatedFile {
    context (ErrorAware)
    operator fun invoke(filePath: String): String =
        File(filePath).run {
            if (!exists()) {
                raise(AawError("$filePath does not exist"))
            }

            if (!isFile()) {
                raise(AawError("$filePath is not a file"))
            }
        }.let {
            filePath
        }
}
