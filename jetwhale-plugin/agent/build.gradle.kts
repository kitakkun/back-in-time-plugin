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

    // JetWhale publishes no iosX64 klib, so this module cannot offer that target either.
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            // The app hosting this plugin needs both to construct and register it, so both are api.
            api(libs.jetwhaleAgentSdk)
            api(projects.jetwhalePlugin.protocol)
            api(projects.core.runtime)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
        }
    }
}

android {
    namespace = "com.kitakkun.jetwhale.plugins.backintime.agent"
    compileSdk = 34
}

backintimePublication {
    artifactId = "jetwhale-plugin-agent"
}
