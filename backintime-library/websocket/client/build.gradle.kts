plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    `maven-publish`
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
            implementation(project(":backintime-library:websocket:event"))
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.server.test.host)
            implementation(libs.ktor.server.websockets)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "com.github.kitakkun.backintime.websocket.client"
    compileSdk = 34
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "backintime-websocket-client"
        }
    }
}
