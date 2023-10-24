package io.dotanuki.arw.features.comparison

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.dotanuki.arw.core.domain.errors.ErrorAware
import io.dotanuki.arw.core.infrastructure.android.AndroidArtifactAnalyser
import io.dotanuki.arw.core.infrastructure.cli.ErrorReporter
import io.dotanuki.arw.core.infrastructure.filesystem.ValidatedFile
import io.dotanuki.arw.features.common.ArtifactBaseline
import java.io.File

context (CompareContext)
class CompareCommand : CliktCommand(
    help = "arw compare -t/--target <path/to/target> -b/--baseline <path/to/baseline>",
    name = "compare"
) {

    private val target: String by option("-t", "--target").required()
    private val baseline: String by option("-b", "--baseline").required()
    private val debugMode by option("--stacktrace").flag(default = false)

    override fun run() {
        ErrorReporter.printStackTraces = debugMode
        recover(::performComparison, ErrorReporter::reportFailure)
    }

    context (ErrorAware)
    private fun performComparison() {
        val current = AndroidArtifactAnalyser.analyse(ValidatedFile(target))
        val baselineFile = File(ValidatedFile(baseline))
        val reference = tomlSerializer.decodeFromString(ArtifactBaseline.serializer(), baselineFile.readText())
        val comparison = ArtifactsComparator.compare(current, reference.asArtifact())
        ComparisonReporter.reportChanges(comparison)
    }
}
