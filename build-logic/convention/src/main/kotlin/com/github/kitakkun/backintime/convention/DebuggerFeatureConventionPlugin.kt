package com.github.kitakkun.backintime.convention

import com.github.kitakkun.backintime.convention.extension.compose
import com.github.kitakkun.backintime.convention.extension.libs
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
                        implementation(project(":backintime-debugger:data"))
                        implementation(libs.findBundle("voyager").get())
                        implementation(libs.findBundle("composeIcons").get())
                        implementation(libs.findLibrary("koin-core").get())
                        implementation(compose.materialIconsExtended)
                        implementation(compose.material3)
                    }
                    jvmMain.dependencies {
                        implementation(compose.uiTooling)
                        // need this to use Dispatchers.Main
                        implementation(libs.findLibrary("kotlinx-coroutines-swing").get())
                    }
                }
            }
        }
    }
}
