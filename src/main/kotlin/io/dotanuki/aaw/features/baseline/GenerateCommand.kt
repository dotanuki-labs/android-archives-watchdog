package io.dotanuki.aaw.features.baseline

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.dotanuki.aaw.core.android.AndroidArtifactAnalyser
import io.dotanuki.aaw.core.cli.ErrorReporter
import io.dotanuki.aaw.core.errors.ErrorAware
import io.dotanuki.aaw.core.filesystem.ValidatedFile
import io.dotanuki.aaw.core.toml.WatchdogConfig

context (BaselineContext)
class GenerateCommand : CliktCommand(
    help = "aaw generate -a/--archive <path/to/archive> -t/--trust <package1,package2,...>",
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
