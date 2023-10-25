package io.dotanuki.arw.features.baseline

import com.github.ajalt.mordant.rendering.TextColors.cyan
import io.dotanuki.arw.core.cli.emptyLine
import io.dotanuki.arw.core.toml.SerializableBaseline
import java.nio.file.Paths
import kotlin.io.path.writeText

object BaselineWriter {

    context (BaselineContext)
    fun write(baseline: SerializableBaseline, filename: String) {
        val toml = tomlSerializer.encodeToString(SerializableBaseline.serializer(), baseline)

        Paths.get(filename).run {
            writeText(toml)
            terminal.emptyLine()
            terminal.println("Baseline available at : ${cyan(toFile().toString())}")
        }
    }
}
