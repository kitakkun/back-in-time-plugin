plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    `maven-publish`
}

kotlin {
    jvmToolchain(8)

    jvm()
    androidTarget {
        publishLibraryVariants("debug")
    }

    sourceSets {
        commonMain
    }
}

android {
    compileSdk = 34
    namespace = "com.github.kitakkun.backintime.annotations"
}
