plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.backintimePublication)
}

kotlin {
    jvmToolchain(17)

    // JVM-based targets only. JetWhale's Kotlin/Native klibs are built with Kotlin 2.4 (klib
    // abi_version 2.4.0), which the 2.2 Kotlin/Native compiler this repository uses cannot read --
    // and klib ABI has no equivalent of -Xskip-metadata-version-check. The iOS targets of
    // :core:runtime are unaffected; only this JetWhale bridge is restricted.
    jvm()
    androidTarget { publishAllLibraryVariants() }

    // JetWhale is published with a newer Kotlin than this repository builds with; see
    // jetwhale-host-module.gradle.kts for the rationale.
    compilerOptions {
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }

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
