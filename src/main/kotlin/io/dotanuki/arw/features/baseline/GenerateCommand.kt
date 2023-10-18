package io.dotanuki.arw.features.baseline

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.dotanuki.arw.core.domain.errors.ErrorAware
import io.dotanuki.arw.core.infrastructure.android.AndroidArtifactAnalyser
import io.dotanuki.arw.core.infrastructure.cli.ErrorReporter

context (BaselineContext)
class GenerateCommand : CliktCommand(
    help = "arw generate -t/--target <path/to/target>",
    name = "generate"
) {

    private val target: String by option("-t", "--target").required()

    override fun run() = recover(::extractBaseline, ErrorReporter::reportFailure)

    context (ErrorAware)
    private fun extractBaseline() {
        val analysed = AndroidArtifactAnalyser.overview(target)
        val outputFile = "${analysed.applicationId}.toml"
        val baseline = analysed.let { ArtifactBaseline(it.androidPermissions, it.androidFeatures) }
        BaselineWriter.write(baseline, outputFile)
    }
}
