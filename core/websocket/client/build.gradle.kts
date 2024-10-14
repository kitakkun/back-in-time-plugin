plugins {
    alias(libs.plugins.kotlinMultiplatform)
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

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.websocket.event)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.server.test.host)
            implementation(libs.ktor.server.websockets)
        }
    }
}

android {
    namespace = "io.github.kitakkun.backintime.core.websocket.client"
    compileSdk = 34
}

backintimePublication {
    artifactId = "core-websocket-client"
}
