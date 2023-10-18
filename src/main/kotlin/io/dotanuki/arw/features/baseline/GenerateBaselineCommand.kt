package io.dotanuki.arw.features.baseline

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import io.dotanuki.arw.core.domain.errors.ErrorAware
import io.dotanuki.arw.core.infrastructure.android.AndroidArtifactAnalyser
import io.dotanuki.arw.core.infrastructure.cli.ErrorReporter
import net.peanuuutz.tomlkt.Toml
import java.nio.file.Paths
import kotlin.io.path.writeText

class GenerateBaselineCommand : CliktCommand(
    help = "arw generate -t/--target <path/to/target>",
    name = "generate"
) {

    private val target: String by option("-t", "--target").required()

    override fun run() = recover(::extractBaseline, ErrorReporter::reportFailure)

    context (ErrorAware)
    private fun extractBaseline() {
        val analysed = AndroidArtifactAnalyser.overview(target)
        val baseline = ArtifactBaseline(
            analysed.androidPermissions,
            analysed.androidFeatures
        )

        val toml = Toml.encodeToString(ArtifactBaseline.serializer(), baseline)

        // TODO : configure proper output location
        Paths.get("${analysed.applicationId}.toml").writeText(toml)
    }
}
