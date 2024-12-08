package com.kitakkun.backintime.compiler.common

import com.kitakkun.backintime.plugin.BackInTimeCompilerOptionKey
import org.jetbrains.kotlin.config.CompilerConfigurationKey

object BackInTimeCompilerConfigurationKey {
    val ENABLED = CompilerConfigurationKey.create<Boolean>(BackInTimeCompilerOptionKey.ENABLED)
}
