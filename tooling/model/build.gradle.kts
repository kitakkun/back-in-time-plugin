plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.backintimePublication)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    jvm()
    jvmToolchain(17)
    js(IR) {
        nodejs()
        generateTypeScriptDefinitions()
        binaries.library()
    }

    androidTarget()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets.commonMain.dependencies {
        implementation(libs.kotlinx.serialization.json)
    }
}

backintimePublication {
    artifactId = "tooling-model"
}

android {
    namespace = "com.kitakkun.backintime.tooling.model"
    compileSdk = 34
}
