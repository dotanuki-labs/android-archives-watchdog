package io.dotanuki.arw.core.infrastructure.filesystem

import io.dotanuki.arw.core.domain.errors.ArwError
import io.dotanuki.arw.core.domain.errors.ErrorAware
import java.io.File

object ValidatedFile {

    context (ErrorAware)
    operator fun invoke(filePath: String): String =
        File(filePath).run {
            if (!exists()) {
                raise(ArwError("$filePath does not exist"))
            }

            if (!isFile()) {
                raise(ArwError("$filePath is not a file"))
            }
        }.let {
            filePath
        }
}
