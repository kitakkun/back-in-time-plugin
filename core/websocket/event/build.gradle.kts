plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.backintimePublication)
}

kotlin {
    jvm()
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvmToolchain(17)

    js(IR) {
        nodejs()
        generateTypeScriptDefinitions()
        binaries.library()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

android {
    namespace = "io.github.kitakkun.backintime.core.websocket.event"
    compileSdk = 34
}

backintimePublication {
    artifactId = "core-websocket-event"
}
