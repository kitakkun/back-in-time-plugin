package io.github.kitakkun.backintime

import io.github.kitakkun.backintime.extension.BackInTimeExtension
import io.github.kitakkun.backintime.plugin.BackInTimeCompilerOptionKey
import io.github.kitakkun.backintime.plugin.BackInTimePluginConsts
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class BackInTimePlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        with(target) {
            extensions.create(
                "backInTime",
                BackInTimeExtension::class.java,
            )

            // FIXME: can get a version of back-in-time plugin from some reliable source?
            val version = "1.0.0"
            val annotationDependencyNotation = "io.github.kitakkun.backintime:backintime-annotations:$version"
            val runtimeDependencyNotation = "io.github.kitakkun.backintime:backintime-runtime:$version"
            val eventDependencyNotation = "io.github.kitakkun.backintime:backintime-websocket-event:$version"

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
        return kotlinCompilation.project.plugins.hasPlugin(BackInTimePlugin::class.java)
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val extension = kotlinCompilation.project.extensions.findByType(BackInTimeExtension::class.java) ?: BackInTimeExtension()
        return kotlinCompilation.target.project.provider {
            listOf(SubpluginOption(key = BackInTimeCompilerOptionKey.ENABLED, value = extension.enabled.toString()))
        }
    }

    override fun getCompilerPluginId(): String {
        return BackInTimePluginConsts.PLUGIN_ID
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = "io.github.kitakkun.backintime",
            artifactId = "backintime-compiler",
            version = "1.0.0",
        )
    }
}
