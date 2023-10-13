package io.dotanuki.arw

import com.github.ajalt.clikt.core.subcommands
import io.dotanuki.arw.cli.ARWCommand
import io.dotanuki.arw.overview.OverviewCommand

fun main(args: Array<String>) {
    ARWCommand()
        .subcommands(OverviewCommand())
        .main(args)
}
