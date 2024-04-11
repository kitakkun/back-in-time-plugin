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
            implementation(project(":backintime-library:websocket:event"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.server.cio)
            implementation(libs.ktor.server.websockets)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.uuid)
        }
    }
}

android {
    namespace = "com.github.kitakkun.backintime.websocket.server"
    compileSdk = 34
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "backintime-websocket-server"
        }
    }
}
