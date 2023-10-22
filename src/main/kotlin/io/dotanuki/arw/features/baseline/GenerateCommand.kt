package io.dotanuki.arw.features.baseline

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.dotanuki.arw.core.domain.errors.ErrorAware
import io.dotanuki.arw.core.infrastructure.android.AndroidArtifactAnalyser
import io.dotanuki.arw.core.infrastructure.cli.ErrorReporter
import io.dotanuki.arw.features.common.ArtifactBaseline
import io.dotanuki.arw.features.common.ValidatedFile

context (BaselineContext)
class GenerateCommand : CliktCommand(
    help = "arw generate -t/--target <path/to/target>",
    name = "generate"
) {

    private val target: String by option("-t", "--target").required()

    override fun run() = recover(::extractBaseline, ErrorReporter::reportFailure)

    context (ErrorAware)
    private fun extractBaseline() {
        val analysed = AndroidArtifactAnalyser.analyse(ValidatedFile(target))
        val outputFile = "${analysed.applicationId}.toml"

        val baseline = ArtifactBaseline.from(analysed)
        BaselineWriter.write(baseline, outputFile)
    }
}
