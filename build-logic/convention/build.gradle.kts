plugins {
    `kotlin-dsl`
    alias(libs.plugins.buildconfig)
}

gradlePlugin {
    plugins {
        register("io.github.kitakkun.backintime.conventions.ktlint") {
            id = "backintime.lint"
            implementationClass = "io.github.kitakkun.backintime.convention.LintConventionPlugin"
        }
        register("io.github.kitakkun.backintime.conventions.publication") {
            id = "backintime.publication"
            implementationClass = "io.github.kitakkun.backintime.convention.BackInTimePublicationPlugin"
        }
        register("io.github.kitakkun.backintime.convention.debugger.feature") {
            id = "backintime.debugger.feature"
            implementationClass = "io.github.kitakkun.backintime.convention.debugger.DebuggerFeatureConventionPlugin"
        }
        register("io.github.kitakkun.backintime.convention.debugger.featurecommon") {
            id = "backintime.debugger.featurecommon"
            implementationClass = "io.github.kitakkun.backintime.convention.debugger.FeatureCommonConventionPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.ktlint.gradle)
    compileOnly(libs.maven.publish)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.jetbrains.compose.gradle.plugin)
}

buildConfig {
    buildConfigField("VERSION", libs.versions.backintime.get())
}
