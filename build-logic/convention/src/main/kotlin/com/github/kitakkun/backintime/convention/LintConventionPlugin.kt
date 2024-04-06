package com.github.kitakkun.backintime.convention

import com.github.kitakkun.backintime.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

class LintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jlleitschuh.gradle.ktlint")
            }

            configure<KtlintExtension> {
                version.set(libs.findVersion("ktlint").get().requiredVersion)
                ignoreFailures.set(true)
                reporters {
                    reporter(ReporterType.CHECKSTYLE)
                }
            }
        }
    }
}