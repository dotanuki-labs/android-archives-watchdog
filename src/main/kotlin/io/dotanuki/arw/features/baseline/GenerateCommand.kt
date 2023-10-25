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
    help = "arw generate -a/--archive <path/to/archive> -t/--trust <packages>",
    name = "generate"
) {

    private val pathToArchive: String by option("-a", "--archive").required()
    private val trustedPackages: String? by option("-t", "--trust")
    private val debugMode by option("--stacktrace").flag(default = false)

    override fun run() {
        ErrorReporter.printStackTraces = debugMode
        recover(::extractBaseline, ErrorReporter::reportFailure)
    }

    context (ErrorAware)
    private fun extractBaseline() {
        val analysed = AndroidArtifactAnalyser.analyse(ValidatedFile(pathToArchive))
        val baseline = WatchdogConfig.from(analysed, ValidatedPackages(trustedPackages))
        val outputFile = "${analysed.applicationId}.toml"
        BaselineWriter.write(baseline, outputFile)
    }
}
