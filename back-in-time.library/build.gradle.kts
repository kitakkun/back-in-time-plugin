plugins {
    kotlin("multiplatform")
    id("com.android.library")
    `maven-publish`
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    jvmToolchain(8)

    jvm()
    androidTarget {
        publishAllLibraryVariants()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                compileOnly("com.facebook.flipper:flipper:0.233.0")
            }
        }
        val jvmMain by getting {
            dependsOn(commonMain)
        }
    }
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        targetSdk = 34
        minSdk = 21
    }
    namespace = "com.github.kitakkun.back_in_time"

}

publishing {
    publications {
        withType<MavenPublication> {
            groupId = "com.github.kitakkun.back_in_time"
            version = "1.0.0"
        }
    }
    repositories {
        mavenLocal()
    }
}
