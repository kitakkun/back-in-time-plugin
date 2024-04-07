package com.github.kitakkun.backintime.convention

import com.github.kitakkun.backintime.convention.extension.compose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class DebuggerFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.multiplatform")
            }

            configure<KotlinMultiplatformExtension> {
                jvm()

                with(sourceSets) {
                    commonMain.dependencies {
                        implementation(project(":backintime-debugger:ui"))
                    }
                    jvmMain.dependencies {
                        implementation(compose.desktop.currentOs)
                        implementation(compose.material3)
                    }
                }
            }
        }
    }
}
