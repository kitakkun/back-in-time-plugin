package com.github.kitakkun.backintime

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
        val extension = kotlinCompilation.project.extensions.findByType(BackInTimeExtension::class.java)
            ?: BackInTimeExtension()
        if (extension.enabled && extension.annotations.isEmpty()) {
            error("Back-in-time plugin is enabled but no annotations are specified.")
        }

        val annotationOptions = extension.annotations.map {
            SubpluginOption(key = "myPluginAnnotation", value = it)
        }
        val enabledOption = SubpluginOption(
            key = "enabled",
            value = extension.enabled.toString(),
        )
        return kotlinCompilation.target.project.provider {
            listOf(enabledOption) + annotationOptions
        }
    }

    override fun getCompilerPluginId(): String {
        return "back-in-time-plugin"
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = "com.github.kitakkun",
            artifactId = "back-in-time-kotlin-plugin",
            version = "1.0.0",
        )
    }
}
