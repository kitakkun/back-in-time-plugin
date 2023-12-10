package com.github.kitakkun.backintime.compiler.backend.utils

import org.jetbrains.kotlin.name.Name

fun Name.getPropertyName(): String {
    return this.asString()
        .removePrefix("<set-")
        .removePrefix("<get-")
        .removeSuffix(">")
}

fun Name.isGetterName(): Boolean {
    return this.asString().startsWith("<get-")
}

fun Name.isSetterName(): Boolean {
    return this.asString().startsWith("<set-")
}
