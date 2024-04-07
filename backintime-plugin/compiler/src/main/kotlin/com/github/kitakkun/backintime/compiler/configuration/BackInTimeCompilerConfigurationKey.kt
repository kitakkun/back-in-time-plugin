package com.github.kitakkun.backintime.compiler.configuration

import com.github.kitakkun.backintime.plugin.BackInTimeCompilerOptionKey
import com.github.kitakkun.backintime.plugin.extension.ValueContainerConfig
import org.jetbrains.kotlin.config.CompilerConfigurationKey

object BackInTimeCompilerConfigurationKey {
    val ENABLED = CompilerConfigurationKey.create<Boolean>(BackInTimeCompilerOptionKey.ENABLED)
    val VALUE_CONTAINER = CompilerConfigurationKey.create<List<ValueContainerConfig>>(BackInTimeCompilerOptionKey.VALUE_CONTAINER)
}
