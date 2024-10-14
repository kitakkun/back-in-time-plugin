plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.backintimeLint)
    alias(libs.plugins.backintimePublication)
}

kotlin {
    jvmToolchain(17)

    jvm()
    androidTarget { publishAllLibraryVariants() }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain
    }
}

android {
    compileSdk = 34
    namespace = "io.github.kitakkun.backintime.annotations"
}

backintimePublication {
    artifactId = "core-annotations"
}