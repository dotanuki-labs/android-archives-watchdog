package io.dotanuki.arw.overview

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.switch
import io.dotanuki.arw.shared.analyser.AndroidArtifactAnalyser

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

    private val reporter by lazy { OverviewReporter() }
    private val analyser by lazy { AndroidArtifactAnalyser() }

    override fun run() {
        val overview = analyser.overview(target)
        reporter.report(overview, format)
    }
}
