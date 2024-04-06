package com.github.kitakkun.backintime

import com.github.kitakkun.backintime.extension.BackInTimeExtension
import com.github.kitakkun.backintime.plugin.BackInTimeCompilerOptionKey
import com.github.kitakkun.backintime.plugin.BackInTimePluginConsts
import com.google.auto.service.AutoService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import java.util.Base64

@Suppress("unused")
@AutoService(KotlinCompilerPluginSupportPlugin::class)
class BackInTimePlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        with(target) {
            extensions.create(
                "backInTime",
                BackInTimeExtension::class.java,
            )

            // FIXME: can get a version of back-in-time plugin from some reliable source?
            val version = "1.0.0"
            val annotationDependencyNotation = "com.github.kitakkun.backintime:backintime-annotations:$version"
            val runtimeDependencyNotation = "com.github.kitakkun.backintime:backintime-runtime:$version"

            when (kotlinExtension) {
                is KotlinSingleTargetExtension<*> -> {
                    dependencies.add("implementation", annotationDependencyNotation)
                    dependencies.add("implementation", runtimeDependencyNotation)
                }

                is KotlinMultiplatformExtension -> {
                    dependencies.add("commonMainImplementation", annotationDependencyNotation)
                    dependencies.add("commonMainImplementation", runtimeDependencyNotation)
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
            listOf(
                SubpluginOption(key = BackInTimeCompilerOptionKey.ENABLED, value = extension.enabled.toString()),
            ).plus(
                extension.valueContainers
                    .map { Json.encodeToString(it) }
                    .map { Base64.getEncoder().encodeToString(it.toByteArray(Charsets.UTF_8)) }
                    .map {
                        SubpluginOption(key = BackInTimeCompilerOptionKey.VALUE_CONTAINER, value = it)
                    },
            )
        }
    }

    override fun getCompilerPluginId(): String {
        return BackInTimePluginConsts.pluginId
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = "com.github.kitakkun.backintime",
            artifactId = "backintime-compiler",
            version = "1.0.0",
        )
    }
}
