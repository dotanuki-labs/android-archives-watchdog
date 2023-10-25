package io.dotanuki.arw.core.cli

import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.terminal.Terminal
import io.dotanuki.arw.core.errors.ArwError
import kotlin.system.exitProcess

object ErrorReporter {

    var printStackTraces: Boolean = false

    private val terminal by lazy { Terminal() }

    fun reportFailure(surfaced: ArwError) {
        terminal.emptyLine()
        terminal.println(red(surfaced.description))

        if (printStackTraces) {
            val trace = surfaced.wrapped ?: return
            terminal.emptyLine()
            trace.printStackTrace()
        }

        terminal.emptyLine()
        exitProcess(ExitCodes.FAILURE)
    }
}
