plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
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
        commonMain.dependencies {
            implementation(projects.core.websocket.client)
            implementation(projects.core.websocket.event)
            implementation(projects.tooling.core.model)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.uuid)
        }
    }
}

android {
    compileSdk = 34
    namespace = "com.kitakkun.backintime.core.runtime"
}

backintimePublication {
    artifactId = "core-runtime"
}
