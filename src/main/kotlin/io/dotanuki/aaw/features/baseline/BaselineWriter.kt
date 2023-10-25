package io.dotanuki.aaw.features.baseline

import com.github.ajalt.mordant.rendering.TextColors.cyan
import io.dotanuki.aaw.core.cli.emptyLine
import io.dotanuki.aaw.core.toml.WatchdogConfig
import java.nio.file.Paths
import kotlin.io.path.writeText

object BaselineWriter {

    context (BaselineContext)
    fun write(baseline: WatchdogConfig, filename: String) {
        val toml = tomlSerializer.encodeToString(WatchdogConfig.serializer(), baseline)

        Paths.get(filename).run {
            writeText(toml)
            terminal.emptyLine()
            terminal.println("Baseline available at : ${cyan(toFile().toString())}")
        }
    }
}
