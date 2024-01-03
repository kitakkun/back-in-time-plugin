plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    `maven-publish`
}

kotlin {
    jvmToolchain(8)

    jvm()
    androidTarget()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        androidMain {
            dependencies {
                compileOnly(libs.flipper)
            }
        }
        jvmMain {}
        jvmTest {}
    }
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        targetSdk = 34
        minSdk = 21
    }
    namespace = "com.github.kitakkun.backintime"
}

publishing {
    publications {
        withType<MavenPublication> {
            groupId = "com.github.kitakkun.backintime"
            version = "1.0.0"
        }
    }
    repositories {
        mavenLocal()
    }
}
