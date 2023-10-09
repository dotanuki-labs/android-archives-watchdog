package io.dotanuki.arw.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context

class ARWCommand : CliktCommand(
    name = "arw <command> <options>",
    help = "A watchdog for your Android releasable artifacts",
    printHelpOnEmptyArgs = true,
    epilog =
    """
    Examples:
    
    ```
    $> arw version
    $> arw analyse
    $> arw generate <option>
    $> arw compare <options>
    ```
    """.trimIndent()
) {

    init {
        context {
            helpOptionNames = emptySet()
        }
    }

    override fun run() = Unit
}
