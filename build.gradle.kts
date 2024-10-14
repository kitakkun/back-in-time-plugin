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
    alias(libs.plugins.buildconfig) apply false
    alias(libs.plugins.maven.publish) apply false
    // convention plugin
    alias(libs.plugins.backintimeLint) apply false
    alias(libs.plugins.backintimePublication) apply false
}
