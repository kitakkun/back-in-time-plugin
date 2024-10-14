package io.github.kitakkun.backintime.compiler.configuration

import io.github.kitakkun.backintime.plugin.BackInTimeCompilerOptionKey
import org.jetbrains.kotlin.config.CompilerConfigurationKey

object BackInTimeCompilerConfigurationKey {
    val ENABLED = CompilerConfigurationKey.create<Boolean>(BackInTimeCompilerOptionKey.ENABLED)
}
