package io.dotanuki.arw.overview

import arrow.core.raise.recover
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.switch
import io.dotanuki.arw.shared.analyser.AndroidArtifactAnalyser
import io.dotanuki.arw.shared.errors.ArwError
import io.dotanuki.arw.shared.errors.ErrorAware

class OverviewCommand : CliktCommand(
    help = "arw overview [--console | --json] ",
    name = "overview"
) {

    private val switches = listOf(
        "--json" to "json",
        "--console" to "console"
    )

    private val format: String by option()
        .switch(*switches.toTypedArray())
        .default("console")

    private val target: String by option("-t", "--target").required()

    override fun run() = recover(::extractOverview, ::reportFailure)

    context (ErrorAware)
    private fun extractOverview() {
        val overview = AndroidArtifactAnalyser.overview(target)
        OverviewReporter.reportSuccess(overview, format)
    }

    private fun reportFailure(incoming: ArwError) {
        OverviewReporter.reportFailure(incoming)
    }
}
