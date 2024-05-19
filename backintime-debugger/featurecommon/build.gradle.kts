plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.backintimeLint)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":backintime-debugger:ui"))
            implementation(project(":backintime-debugger:data"))
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.preview)
            implementation(compose.materialIconsExtended)
            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeVM)
            implementation(libs.kotlinx.datetime)
        }
    }
}
