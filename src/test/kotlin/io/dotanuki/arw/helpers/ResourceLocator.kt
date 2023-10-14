package io.dotanuki.arw.helpers

fun Any.fixtureFromResources(target: String): String =
    requireNotNull(javaClass.classLoader.getResource(target)).path
