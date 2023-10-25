package io.dotanuki.aaw.helpers

fun Any.fixtureFromResources(target: String): String =
    requireNotNull(javaClass.classLoader.getResource(target)).path
