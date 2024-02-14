plugins {
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

group = "com.github.kitakkun.backintime"
version = "1.0.0"

subprojects {
    group = rootProject.group
    version = rootProject.version
}
