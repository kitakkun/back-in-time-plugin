package com.github.kitakkun.backintime

import com.github.kitakkun.backintime.plugin.BackInTimeCompilerOptionKey
import com.github.kitakkun.backintime.plugin.BackInTimePluginConsts
import com.google.auto.service.AutoService
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@Suppress("unused")
@AutoService(KotlinCompilerPluginSupportPlugin::class)
class BackInTimePlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        target.extensions.create(
            "backInTime",
            BackInTimeExtension::class.java,
        )
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return kotlinCompilation.project.plugins.hasPlugin(BackInTimePlugin::class.java)
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val extension = kotlinCompilation.project.extensions.findByType(BackInTimeExtension::class.java) ?: BackInTimeExtension()
        return kotlinCompilation.target.project.provider {
            listOf(
                SubpluginOption(key = BackInTimeCompilerOptionKey.ENABLED, value = extension.enabled.toString()),
            ) + extension.capturedCalls.map {
                SubpluginOption(key = BackInTimeCompilerOptionKey.CAPTURED_CALLS, value = it)
            } + extension.valueGetters.map {
                SubpluginOption(key = BackInTimeCompilerOptionKey.VALUE_GETTERS, value = it)
            }
        }
    }

    override fun getCompilerPluginId(): String {
        return BackInTimePluginConsts.pluginId
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = "com.github.kitakkun",
            artifactId = "back-in-time-kotlin-plugin",
            version = "1.0.0",
        )
    }
}
