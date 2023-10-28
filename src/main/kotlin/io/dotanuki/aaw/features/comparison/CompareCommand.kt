package io.dotanuki.aaw.features.comparison

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.dotanuki.aaw.core.android.AndroidArtifactAnalyser
import io.dotanuki.aaw.core.cli.ErrorReporter
import io.dotanuki.aaw.core.cli.ExitCodes
import io.dotanuki.aaw.core.errors.ErrorAware
import io.dotanuki.aaw.core.filesystem.ValidatedFile
import io.dotanuki.aaw.core.toml.ValidatedTOML
import kotlin.system.exitProcess

context (CompareContext)
class CompareCommand : CliktCommand(
    help = "aaw compare -a/--archive <path/to/archive> -b/--baseline <path/to/baseline>",
    name = "compare"
) {

    private val pathToArchive: String by option("-a", "--archive").required()
    private val pathToBaseline: String by option("-b", "--baseline").required()
    private val exitWithFailure by option("--fail").flag(default = false)
    private val withStacktraces by option("--stacktrace").flag(default = false)

    override fun run() {
        ErrorReporter.printStackTraces = withStacktraces
        recover(::performComparison, ErrorReporter::reportFailure)
    }

    context (ErrorAware)
    private fun performComparison() {
        val current = AndroidArtifactAnalyser.analyse(ValidatedFile(pathToArchive))
        val reference = ValidatedTOML(ValidatedFile(pathToBaseline))
        val comparison = ArtifactsComparator.compare(current, reference.asBaseline())
        ComparisonReporter.reportChanges(comparison)

        if (exitWithFailure && comparison.isNotEmpty()) {
            exitProcess(ExitCodes.FAILURE)
        }
    }
}
