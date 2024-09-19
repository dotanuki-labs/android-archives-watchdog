/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.core.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.context
import com.github.ajalt.mordant.rendering.TextColors.cyan
import com.github.ajalt.mordant.rendering.TextColors.yellow

class AawEntrypoint : CliktCommand(
    name = "aaw",
) {
    init {
        context {
            helpOptionNames = emptySet()
        }
    }

    override fun helpEpilog(context: Context): String =
        """
        
        ${yellow("Where:")}
        
        • ${cyan("overview")} : $HELP_OVERVIEW${"\u0085"}
        • ${cyan("generate")} : $HELP_GENERATE${"\u0085"}
        • ${cyan("compare")} : $HELP_COMPARE
        
        """.trimIndent()

    override val printHelpOnEmptyArgs: Boolean = true

    override fun run() = Unit

    private companion object {
        const val HELP_OVERVIEW = "Dumps basic information about the target APK or AAB"
        const val HELP_GENERATE = "Extracts info about an APK or AAB into a baseline"
        const val HELP_COMPARE = "Compares the target APK or AAB with a baseline"
    }
}
