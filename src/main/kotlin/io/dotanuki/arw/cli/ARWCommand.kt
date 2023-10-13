package io.dotanuki.arw.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.rendering.TextColors.yellow

class ARWCommand : CliktCommand(
    printHelpOnEmptyArgs = true,
    epilog =
    """
    
    ${yellow("Where:")}
    
    • ${cyan("overview")} : $HELP_OVERVIEW${"\u0085"}
    • ${cyan("generate")} : $HELP_GENERATE${"\u0085"}
    • ${cyan("compare")} : $HELP_COMPARE
    
    """.trimIndent()
) {

    init {
        context {
            helpOptionNames = emptySet()
        }
    }

    override fun run() = Unit

    private companion object {
        const val HELP_OVERVIEW = "Dumps basic information about the target APK or AAB"
        const val HELP_GENERATE = "Extracts info about an APK or AAB into a baseline"
        const val HELP_COMPARE = "Compares the target APK or AAB with a baseline"
    }
}
