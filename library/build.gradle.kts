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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        androidMain {
            dependencies {
                compileOnly("com.facebook.flipper:flipper:0.233.0")
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
