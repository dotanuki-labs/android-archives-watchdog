package io.dotanuki.arw.features.baseline

import com.github.ajalt.mordant.rendering.TextColors.cyan
import io.dotanuki.arw.core.infrastructure.cli.emptyLine
import io.dotanuki.arw.features.common.ArtifactBaseline
import java.nio.file.Paths
import kotlin.io.path.writeText

object BaselineWriter {

    context (BaselineContext)
    fun write(baseline: ArtifactBaseline, filename: String) {
        val toml = tomlSerializer.encodeToString(ArtifactBaseline.serializer(), baseline)

        Paths.get(filename).run {
            writeText(toml)
            terminal.emptyLine()
            terminal.println("Baseline available at : ${cyan(toFile().toString())}")
        }
    }
}
