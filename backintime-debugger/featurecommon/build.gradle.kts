plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.backintimeLint)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.navigation.compose)
        }
    }
}
