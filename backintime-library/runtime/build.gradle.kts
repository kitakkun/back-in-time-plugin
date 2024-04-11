plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
    `maven-publish`
}

kotlin {
    jvmToolchain(8)

    jvm()
    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":backintime-library:websocket:client"))
            implementation(project(":backintime-library:websocket:event"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.uuid)
        }
        androidMain.dependencies {
            implementation(libs.flipper)
        }
    }
}

android {
    compileSdk = 34
    namespace = "com.github.kitakkun.backintime.runtime"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "backintime-runtime"
        }
    }
}
