package io.dotanuki.arw.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context

class ARWCommand : CliktCommand(
    name = "arw <command> <options>",
    printHelpOnEmptyArgs = true,
    epilog =
    """
    Available commands:
    
    • analyse : dumps basic information about the target APK or AAB${"\u0085"}
    • generate : extracts info about an APK or AAB into a baseline${"\u0085"}
    • compare : compares the target APK or AAB with a baseline
    
    Examples:
    
    $> arw analyse --path=/path/to/my.aab${"\u0085"}
    $> arw generate --path=/path/to/my.apk --mappings=/path/to/mappings.txt${"\u0085"}
    $> arw compare -p=/path/to/my.apk -b=./arw.toml --o=json
    """.trimIndent()
) {

    init {
        context {
            helpOptionNames = emptySet()
        }
    }

    override fun run() = Unit
}
