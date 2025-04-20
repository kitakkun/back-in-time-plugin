package com.kitakkun.backintime.gradle

import com.autonomousapps.kit.AbstractGradleProject
import com.autonomousapps.kit.GradleBuilder.build
import com.autonomousapps.kit.GradleBuilder.buildAndFail
import com.autonomousapps.kit.Source
import com.autonomousapps.kit.gradle.Kotlin
import com.autonomousapps.kit.gradle.Plugin
import com.kitakkun.backintime.gradle_plugin.BuildConfigForTest
import org.junit.Test

class BackInTimeGradlePluginTest {
    @Test
    fun testEnabled() {
        val project = TestProject(enableCompilerPlugin = true).gradleProject
        build(project.rootDir, ":run")
    }

    @Test
    fun testDisabled() {
        val project = TestProject(enableCompilerPlugin = false).gradleProject
        buildAndFail(project.rootDir, ":run")
    }
}

class TestProject(
    enableCompilerPlugin: Boolean,
) : AbstractGradleProject() {
    val gradleProject = newGradleProjectBuilder()
        .withRootProject {
            withBuildScript {
                sources = listOf(
                    Source.kotlin(
                        """
                        import com.kitakkun.backintime.core.annotations.BackInTime
                        import com.kitakkun.backintime.core.annotations.BackInTimeEntryPoint
                        import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
                        
                        @BackInTimeEntryPoint("localhost", 8080)
                        fun main() {
                            val a = A()                
                            // strong type cast (should be an error if the plugin is not enabled)
                            a as BackInTimeDebuggable
                        }
                        
                        @BackInTime
                        class A {
                            var field: String = "hoge"
                        }

                        """.trimIndent(),
                    ).withPath("", "main").build(),
                )
                kotlin = Kotlin.ofTarget(17)
                plugins(
                    Plugin.application,
                    Plugin("org.jetbrains.kotlin.jvm", BuildConfigForTest.KOTLIN_VERSION),
                    Plugin("org.jetbrains.kotlin.plugin.serialization", BuildConfigForTest.KOTLIN_VERSION),
                    Plugin("com.kitakkun.backintime", BuildConfigForTest.VERSION),
                )
                withGroovy(
                    """
                    application {
                        mainClass = "MainKt"
                    }
                    
                    backInTime {
                        enabled = $enableCompilerPlugin
                    }
                    
                    dependencies {
                        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
                    }
                    """.trimIndent(),
                )
            }
        }
        .write()
}
