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
        androidMain
        jvmMain
        jvmTest
    }
}

android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    compileSdk = 34
    defaultConfig.minSdk = 21
    testOptions.targetSdk = 34
    lint.targetSdk = 34

    namespace = "com.github.kitakkun.backintime"

    dependencies {
        debugImplementation(libs.flipper)
    }
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
