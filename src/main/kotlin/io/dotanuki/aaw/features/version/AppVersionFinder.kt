package io.dotanuki.aaw.features.version

import arrow.core.raise.ensure
import io.dotanuki.aaw.core.errors.AawError
import io.dotanuki.aaw.core.errors.ErrorAware
import java.util.Properties

object AppVersionFinder {

    context (ErrorAware)
    fun find(): AppVersion = try {
        val properties = Properties().apply {
            load(ClassLoader.getSystemClassLoader().getResourceAsStream("versions.properties"))
        }

        val actualVersion = properties["latest"]
        ensure(actualVersion != null) { AawError("Cannot find 'latest' app version") }
        AppVersion(actualVersion.toString())
    } catch (surfaced: Throwable) {
        raise(AawError("Failed when locating app resources", surfaced))
    }
}
