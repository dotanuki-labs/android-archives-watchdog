package io.dotanuki.arw.features.baseline

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.dotanuki.arw.core.android.AndroidArtifactAnalyser
import io.dotanuki.arw.core.cli.ErrorReporter
import io.dotanuki.arw.core.errors.ErrorAware
import io.dotanuki.arw.core.filesystem.ValidatedFile
import io.dotanuki.arw.core.toml.WatchdogConfig

context (BaselineContext)
class GenerateCommand : CliktCommand(
    help = "arw generate -t/--target <path/to/target>",
    name = "generate"
) {

    private val target: String by option("-t", "--target").required()
    private val ignored: String? by option("-i", "--ignore")
    private val debugMode by option("--stacktrace").flag(default = false)

    override fun run() {
        ErrorReporter.printStackTraces = debugMode
        recover(::extractBaseline, ErrorReporter::reportFailure)
    }

    context (ErrorAware)
    private fun extractBaseline() {
        val analysed = AndroidArtifactAnalyser.analyse(ValidatedFile(target))
        val outputFile = "${analysed.applicationId}.toml"

        val baseline = WatchdogConfig.from(analysed, ignored)
        BaselineWriter.write(baseline, outputFile)
    }
}
