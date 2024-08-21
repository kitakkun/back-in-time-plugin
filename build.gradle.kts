plugins {
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    // convention plugin
    alias(libs.plugins.backintimeLint) apply false
    alias(libs.plugins.backintimeFeatureCommon) apply false
    alias(libs.plugins.backintimeDebuggerFeature) apply false
}

group = "io.github.kitakkun.backintime"
version = "1.0.0"

subprojects {
    group = rootProject.group
    version = rootProject.version
}
