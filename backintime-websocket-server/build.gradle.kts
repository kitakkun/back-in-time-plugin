plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    `maven-publish`
}

kotlin {
    jvm()
    androidTarget()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    jvmToolchain(17)

    sourceSets {
        commonMain.dependencies {
            implementation(project(":backintime-websocket-event"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.server.cio)
            implementation(libs.ktor.server.websockets)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.uuid)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.server.test.host)
            implementation(libs.ktor.server.websockets)
        }
    }
}

android {
    namespace = "io.github.kitakkun.backintime.websocket.server"
    compileSdk = 34
}
