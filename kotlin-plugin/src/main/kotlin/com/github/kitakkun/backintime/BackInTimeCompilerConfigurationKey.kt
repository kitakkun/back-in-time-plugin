package com.github.kitakkun.backintime

import org.jetbrains.kotlin.config.CompilerConfigurationKey

object BackInTimeCompilerConfigurationKey {
    val ENABLED = CompilerConfigurationKey.create<Boolean>("my-plugin-enabled")
    val ANNOTATIONS = CompilerConfigurationKey.create<List<String>>("my-plugin-annotations")
}
