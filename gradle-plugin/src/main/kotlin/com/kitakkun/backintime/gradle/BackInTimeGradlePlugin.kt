@file:Suppress("UNUSED")

package com.kitakkun.backintime.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

class BackInTimeGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // access to initialize kotlinVersion (will be used later)
            BackInTimePluginConst.kotlinVersion

            plugins.apply(BackInTimeCompilerPlugin::class.java)

            extensions.create("backInTime", BackInTimeExtension::class.java)

            when (kotlinExtension) {
                is KotlinSingleTargetExtension<*> -> {
                    dependencies.add("implementation", BackInTimePluginConst.CORE_ANNOTATIONS_LIBRARY_DEPENDENCY_NOTATION)
                    dependencies.add("implementation", BackInTimePluginConst.CORE_RUNTIME_LIBRARY_DEPENDENCY_NOTATION)
                }

                is KotlinMultiplatformExtension -> {
                    dependencies.add("commonMainImplementation", BackInTimePluginConst.CORE_ANNOTATIONS_LIBRARY_DEPENDENCY_NOTATION)
                    dependencies.add("commonMainImplementation", BackInTimePluginConst.CORE_RUNTIME_LIBRARY_DEPENDENCY_NOTATION)
                }
            }
        }
    }
}
