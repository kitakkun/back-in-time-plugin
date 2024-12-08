package com.kitakkun.backintime.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class BackInTimeGradlePluginTest {
    @TempDir
    private lateinit var testProjectDir: File
    private lateinit var settingsGradleFile: File
    private lateinit var buildGradleFile: File
    private lateinit var srcDir: File

    @BeforeEach
    fun setup() {
        settingsGradleFile = File(testProjectDir, "settings.gradle.kts")
        buildGradleFile = File(testProjectDir, "build.gradle.kts")
        srcDir = File(testProjectDir, "src/main/kotlin").also { it.mkdirs() }
    }

    @Test
    fun test() {
        settingsGradleFile.writeText(
            """
            pluginManagement {
                repositories {
                    mavenLocal()
                    mavenCentral()
                }
            }
            
            dependencyResolutionManagement {
                repositories {
                    mavenLocal()
                    mavenCentral()
                }
            }
            """.trimIndent(),
        )

        buildGradleFile.writeText(
            """
            plugins {
                id("org.jetbrains.kotlin.jvm") version "2.0.0"
                id("com.kitakkun.backintime")
            }
            
            backInTime {
                enabled = true
            }
            """.trimIndent(),
        )

        val sourceFile = File(srcDir, "A.kt")
        sourceFile.writeText(
            """
            import com.kitakkun.backintime.annotations.BackInTime
            
            @BackInTime
            class StateHolder {
            }
            """.trimIndent(),
        )

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(":build")
            .withPluginClasspath()
            .run()

        println(result.output)
        Assertions.assertEquals(TaskOutcome.SUCCESS, result.task(":build")?.outcome)
    }
}
