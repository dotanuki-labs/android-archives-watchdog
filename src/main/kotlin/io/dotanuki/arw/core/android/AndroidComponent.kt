package io.dotanuki.arw.core.android

data class AndroidComponent(
    val name: String,
    val type: AndroidComponentType
)

fun Set<AndroidComponent>.declaredNames(
    componentType: AndroidComponentType,
    packagesToIgnore: List<String> = emptyList()
): Set<String> =
    asSequence()
        .filter { it.type == componentType }
        .map { it.name }
        .filterNot { it.prefixedWithAny(packagesToIgnore) }
        .toSet()
        .toSortedSet()

private fun String.prefixedWithAny(patterns: List<String>): Boolean {
    patterns.forEach {
        if (startsWith(it)) return true
    }

    return false
}
