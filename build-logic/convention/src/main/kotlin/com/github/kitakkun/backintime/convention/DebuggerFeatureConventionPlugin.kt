package com.github.kitakkun.backintime.convention

import com.github.kitakkun.backintime.convention.extension.compose
import com.github.kitakkun.backintime.convention.extension.ksp
import com.github.kitakkun.backintime.convention.extension.libs
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
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("backintime.lint")
                apply("com.google.devtools.ksp")
            }

            configure<KotlinMultiplatformExtension> {
                jvm()

                compilerOptions.freeCompilerArgs.add("-Xopt-in=org.jetbrains.compose.resources.ExperimentalResourceApi")

                with(sourceSets) {
                    commonMain.dependencies {
                        implementation(project(":backintime-debugger:ui"))
                        implementation(project(":backintime-debugger:featurecommon"))
                        implementation(project(":backintime-debugger:data"))
                        implementation(libs.findBundle("composeIcons").get())
                        implementation(project.dependencies.platform(libs.findLibrary("koin-bom").get()))
                        implementation(libs.findLibrary("koin-core").get())
                        implementation(libs.findLibrary("koin-compose").get())
                        implementation(libs.findLibrary("koin-composeVM").get())
                        implementation(libs.findLibrary("jetbrains-navigation-compose").get())
                        implementation(libs.findLibrary("jetbrains-lifecycle-viewmodel-compose").get())
                        implementation(compose.materialIconsExtended)
                        implementation(compose.material3)
                        implementation(compose.components.resources)
                        compileOnly(libs.findLibrary("koin-annotations").get())
                    }
                    jvmMain.dependencies {
                        implementation(compose.uiTooling)
                        // need this to use Dispatchers.Main
                        implementation(libs.findLibrary("kotlinx-coroutines-swing").get())
                    }
                }

            }

            dependencies {
                add("kspCommonMainMetadata", libs.findLibrary("koin-ksp-compiler").get())
                add("kspJvm", libs.findLibrary("koin-ksp-compiler").get())
            }

            ksp.apply {
                arg("KOIN_CONFIG_CHECK", "true")
                arg("KOIN_DEFAULT_MODULE", "false")
            }
        }
    }
}
