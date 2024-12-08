package com.kitakkun.backintime.gradle

import backintime.gradle_plugin.BuildConfig
import com.kitakkun.backintime.plugin.BackInTimeCompilerOptionKey
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class BackInTimeGradlePlugin : KotlinCompilerPluginSupportPlugin {
    companion object {
        const val VERSION = BuildConfig.VERSION
    }

    override fun apply(target: Project) {
        with(target) {
            extensions.create(
                "backInTime",
                BackInTimeExtension::class.java,
            )

            val annotationDependencyNotation = "com.kitakkun.backintime:core-annotations:$VERSION"
            val runtimeDependencyNotation = "com.kitakkun.backintime:core-runtime:$VERSION"
            val eventDependencyNotation = "com.kitakkun.backintime:core-websocket-event:$VERSION"

            when (kotlinExtension) {
                is KotlinSingleTargetExtension<*> -> {
                    dependencies.add("implementation", annotationDependencyNotation)
                    dependencies.add("implementation", runtimeDependencyNotation)
                    dependencies.add("implementation", eventDependencyNotation)
                }

                is KotlinMultiplatformExtension -> {
                    dependencies.add("commonMainImplementation", annotationDependencyNotation)
                    dependencies.add("commonMainImplementation", runtimeDependencyNotation)
                    dependencies.add("commonMainImplementation", eventDependencyNotation)
                }

                else -> {
                    // do nothing
                }
            }
        }
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        if (kotlinCompilation.compileKotlinTaskName.contains("release", ignoreCase = true)) {
            return false
        }
        return kotlinCompilation.project.plugins.hasPlugin(BackInTimeGradlePlugin::class.java)
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val extension = kotlinCompilation.project.extensions.findByType(BackInTimeExtension::class.java) ?: BackInTimeExtension()
        return kotlinCompilation.target.project.provider {
            listOf(SubpluginOption(key = BackInTimeCompilerOptionKey.ENABLED, value = extension.enabled.toString()))
        }
    }

    override fun getCompilerPluginId(): String = BuildConfig.COMPILER_PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = "com.kitakkun.backintime",
            artifactId = "compiler",
            version = VERSION,
        )
    }
}
