plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("com.github.kitakkun.backintime.conventions.ktlint") {
            id = "backintime.lint"
            implementationClass = "com.github.kitakkun.backintime.convention.LintConventionPlugin"
        }
        register("com.github.kitakkun.backintime.conventions.debugger.feature") {
            id = "backintime.debugger.feature"
            implementationClass = "com.github.kitakkun.backintime.convention.DebuggerFeatureConventionPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.ktlint.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.jetbrains.compose.gradle.plugin)
    compileOnly(libs.ksp.gradle)
}
