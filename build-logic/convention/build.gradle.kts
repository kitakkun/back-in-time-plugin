plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("io.github.kitakkun.backintime.conventions.ktlint") {
            id = "backintime.lint"
            implementationClass = "io.github.kitakkun.backintime.convention.LintConventionPlugin"
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
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.jetbrains.compose.gradle.plugin)
}
