plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
    `maven-publish`
}

kotlin {
    jvmToolchain(8)

    jvm()
    androidTarget {
        publishLibraryVariants("debug")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        androidMain {
            dependencies {
                compileOnly(libs.flipper)
            }
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
