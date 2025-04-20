package com.kitakkun.backintime.gradle

import com.kitakkun.backintime.gradle_plugin.BuildConfig
import org.jetbrains.kotlin.gradle.utils.loadPropertyFromResources

object BackInTimePluginConst {
    const val COMPILER_PLUGIN_ID = "backintime-compiler-plugin"
    const val GROUP_ID = "com.kitakkun.backintime"

    const val CORE_RUNTIME_LIBRARY_DEPENDENCY_NOTATION = "$GROUP_ID:core-runtime:${BuildConfig.VERSION}"
    const val CORE_ANNOTATIONS_LIBRARY_DEPENDENCY_NOTATION = "$GROUP_ID:core-annotations:${BuildConfig.VERSION}"

    val kotlinVersion by lazy { loadKotlinVersion() }
    val kotlinPrefixedVersion by lazy { "${kotlinVersion}-${BuildConfig.VERSION}" }

    private fun loadKotlinVersion(): String {
        return object {}.loadPropertyFromResources("project.properties", "project.version")
    }
}