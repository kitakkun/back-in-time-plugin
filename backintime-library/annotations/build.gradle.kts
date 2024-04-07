plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.backintimeLint)
    `maven-publish`
}

kotlin {
    jvmToolchain(8)

    jvm()
    androidTarget {
        publishLibraryVariants("debug")
    }

    sourceSets {
        commonMain
    }
}

android {
    compileSdk = 34
    namespace = "com.github.kitakkun.backintime.annotations"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "backintime-annotations"
        }
    }
}
