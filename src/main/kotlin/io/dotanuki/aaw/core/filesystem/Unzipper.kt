/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.filesystem

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipFile

object Unzipper {
    fun unzip(
        target: File,
        destination: String,
    ) {
        File(destination).run {
            if (!exists()) {
                mkdirs()
            }
        }

        ZipFile(target).use { unzipEntries(it, destination) }
    }

    private fun unzipEntries(
        zip: ZipFile,
        destination: String,
    ) {
        zip.entries().asSequence().forEach { entry ->
            zip.getInputStream(entry).use { input ->
                val filePath = destination + File.separator + entry.name

                when {
                    !entry.isDirectory -> input.extractFile(filePath)
                    else -> File(filePath).mkdir()
                }
            }
        }
    }

    private fun InputStream.extractFile(destinationPath: String) {
        val outputStream = BufferedOutputStream(FileOutputStream(destinationPath))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (read(bytesIn).also { read = it } != NO_MORE_BITES) {
            outputStream.write(bytesIn, 0, read)
        }
        outputStream.close()
    }

    private const val NO_MORE_BITES = -1
    private const val BUFFER_SIZE = 4096
}
