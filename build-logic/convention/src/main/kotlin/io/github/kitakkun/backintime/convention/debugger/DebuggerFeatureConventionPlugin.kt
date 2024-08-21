package io.github.kitakkun.backintime.convention.debugger

import io.github.kitakkun.backintime.convention.extension.compose
import io.github.kitakkun.backintime.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class DebuggerFeatureConventionPlugin : Plugin<Project> {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.google.devtools.ksp")
            }

            configure<KotlinMultiplatformExtension> {
                jvm()

                sourceSets.commonMain.dependencies {
                    implementation(project(":backintime-debugger:ui"))
                    implementation(project(":backintime-debugger:data"))
                    implementation(project(":backintime-debugger:router"))
                    implementation(project(":backintime-debugger:resources"))
                    implementation(project(":backintime-debugger:featurecommon"))
                    implementation(libs.findBundle("koin").get())
                    implementation(libs.findLibrary("jetbrains-navigation-compose").get())
                    implementation(libs.findLibrary("jetbrains-lifecycle-viewmodel-compose").get())
                    implementation(compose.dependencies.components.resources)
                    implementation(compose.dependencies.material3)
                    implementation(compose.dependencies.materialIconsExtended)
                }

                sourceSets.jvmMain.dependencies {
                    implementation(libs.findLibrary("kotlinx-coroutines-swing").get())
                    implementation(compose.dependencies.desktop.currentOs)
                }

                compilerOptions {
                    freeCompilerArgs.add("-opt-in=org.koin.core.annotation.KoinExperimentalAPI")
                }
            }

            dependencies {
                add("kspCommonMainMetadata", libs.findLibrary("koin-ksp-compiler").get())
                add("kspJvm", libs.findLibrary("koin-ksp-compiler").get())
            }
        }
    }
}
