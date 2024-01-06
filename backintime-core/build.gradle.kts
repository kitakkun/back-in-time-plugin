plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
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
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        androidMain
        jvmMain
        jvmTest
    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    compileSdk = 34
    defaultConfig.minSdk = 21
    testOptions.targetSdk = 34
    lint.targetSdk = 34

    namespace = "com.github.kitakkun.backintime"

    dependencies {
        debugImplementation(libs.flipper)
    }
}
