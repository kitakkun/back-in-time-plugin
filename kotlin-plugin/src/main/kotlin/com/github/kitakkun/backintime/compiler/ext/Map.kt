package com.github.kitakkun.backintime.compiler.ext

import org.checkerframework.checker.units.qual.K

// 毎回書くのが面倒なので
fun <K, V> Map<K?, V>.filterKeysNotNull(): Map<K, V> {
    return this.filterKeys { it != null }.mapKeys { it.key!! }
}

fun <K, V> Map<K, V?>.filterValuesNotNull(): Map<K, V> {
    return this.filterValues { it != null }.mapValues { it.value!! }
}
