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
            // JetWhaleEvent / JetWhaleRequest are part of this module's public API.
            api(libs.jetwhaleProtocolCore)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

android {
    namespace = "com.kitakkun.jetwhale.plugins.backintime.protocol"
    compileSdk = 34
}

backintimePublication {
    artifactId = "jetwhale-plugin-protocol"
}
