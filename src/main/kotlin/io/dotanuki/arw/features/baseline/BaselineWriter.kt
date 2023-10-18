package io.dotanuki.arw.features.baseline

import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.terminal.Terminal
import io.dotanuki.arw.core.infrastructure.cli.emptyLine
import net.peanuuutz.tomlkt.Toml
import java.nio.file.Paths
import kotlin.io.path.writeText

object BaselineWriter {

    private val terminal by lazy { Terminal() }

    fun write(baseline: ArtifactBaseline, filename: String) {
        val toml = Toml.encodeToString(ArtifactBaseline.serializer(), baseline)
        val outputPath = Paths.get(filename)
        outputPath.writeText(toml)

        terminal.emptyLine()
        terminal.println("Baseline available at : ${cyan(outputPath.toFile().toString())}")
    }
}
