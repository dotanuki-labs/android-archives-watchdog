package io.dotanuki.arw.core.infrastructure.cli

import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.ajalt.mordant.terminal.Terminal
import io.dotanuki.arw.core.domain.errors.ArwError
import kotlin.system.exitProcess

object ErrorReporter {

    private val terminal by lazy { Terminal() }

    fun reportFailure(error: ArwError) {
        terminal.emptyLine()
        terminal.println(red(error.description))
        terminal.emptyLine()

        exitProcess(42)
    }
}