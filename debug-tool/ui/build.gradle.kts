plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.backintimeLint)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.material3)
            implementation(compose.preview)
            implementation(compose.ui)
            implementation(compose.foundation)
        }
    }
}
