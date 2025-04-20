package com.kitakkun.backintime.gradle

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class BackInTimeCompilerPlugin : KotlinCompilerPluginSupportPlugin {
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val extension = kotlinCompilation.project.extensions.findByType(BackInTimeExtension::class.java) ?: BackInTimeExtension()
        return kotlinCompilation.target.project.provider {
            listOf(
                SubpluginOption(key = "enabled", value = extension.enabled.toString()),
                SubpluginOption(key = "config", value = extension.configFilePath),
            )
        }
    }

    override fun getCompilerPluginId(): String {
        return BackInTimePluginConst.COMPILER_PLUGIN_ID
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        if (kotlinCompilation.compileKotlinTaskName.contains("release", ignoreCase = true)) {
            return false
        }
        return kotlinCompilation.target.project.extensions.getByType(BackInTimeExtension::class.java).enabled
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = BackInTimePluginConst.GROUP_ID,
            artifactId = "compiler-cli",
            version = BackInTimePluginConst.kotlinPrefixedVersion,
        )
    }
}