plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.backintimeLint)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
        }
    }
}
