plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.backintimePublication)
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
            implementation(projects.core.websocket.event)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.server.cio)
            implementation(libs.ktor.server.websockets)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.uuid)
        }
        commonTest.dependencies {
            implementation(projects.core.websocket.client)
            implementation(libs.kotlin.test)
            implementation(libs.ktor.server.test.host)
            implementation(libs.ktor.server.websockets)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "com.kitakkun.backintime.core.websocket.server"
    compileSdk = 34
}

backintimePublication {
    artifactId = "core-websocket-server"
}
