package io.dotanuki.arw.overview

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.switch

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

    private val reporter by lazy { OverviewReporter() }
    private val overviewEvaluator by lazy { OverviewEvaluator() }

    override fun run() {
        val overview = overviewEvaluator.evaluate()
        reporter.report(overview, format)
    }
}
