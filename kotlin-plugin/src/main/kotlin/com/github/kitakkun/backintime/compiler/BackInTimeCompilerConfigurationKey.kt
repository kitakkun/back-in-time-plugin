package com.github.kitakkun.backintime.compiler

import com.github.kitakkun.backintime.plugin.BackInTimeCompilerOptionKey
import org.jetbrains.kotlin.config.CompilerConfigurationKey

object BackInTimeCompilerConfigurationKey {
    val ENABLED = CompilerConfigurationKey.create<Boolean>(BackInTimeCompilerOptionKey.ENABLED)
    val CAPTURED_CALLS = CompilerConfigurationKey.create<List<String>>(BackInTimeCompilerOptionKey.CAPTURED_CALLS)
    val VALUE_GETTERS = CompilerConfigurationKey.create<List<String>>(BackInTimeCompilerOptionKey.VALUE_GETTERS)
    val VALUE_SETTERS = CompilerConfigurationKey.create<List<String>>(BackInTimeCompilerOptionKey.VALUE_SETTERS)
}
