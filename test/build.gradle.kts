import com.github.kitakkun.backintime.BackInTimeExtension

buildscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("com.github.kitakkun:back-in-time-plugin:1.0.0")
    }
}

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

// because back-in-time-plugin is published to mavenLocal
// we must apply back-in-time-plugin here instead of plugins block
apply(plugin = "back-in-time-plugin")

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    jvmToolchain(8)
    sourceSets.all {
        languageSettings.languageVersion = "2.0"
    }

    jvm()
    androidTarget()

    sourceSets {
        commonMain {
            dependencies {
                implementation("com.github.kitakkun.backintime:back-in-time.library:1.0.0")
                // coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            }
        }
        commonTest {
            kotlin {
                setSrcDirs(listOf("src/commonTest/kotlin", "build/classes/kotlin/commonMain"))
            }
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")
            }
        }
        val androidMain by getting() {
            dependsOn(commonMain.get())
            dependencies {
            }
        }
        val androidUnitTest by getting() {
            dependsOn(androidMain)
            dependencies {
                implementation("org.robolectric:robolectric:4.11.1")
            }
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
    namespace = "com.github.kitakkun.backintime.test"
}

configure<BackInTimeExtension> {
    enabled = true
    capturedCalls += listOf(
        "com.github.kitakkun.backintime.commonTest.GradleConfiguredValueContainer:<set-value>",
        "com.github.kitakkun.backintime.commonTest.GradleConfiguredValueContainer:update",
    )
    valueGetters += listOf(
        "com.github.kitakkun.backintime.commonTest.GradleConfiguredValueContainer:<get-value>",
    )
    valueSetters += listOf(
        "com.github.kitakkun.backintime.commonTest.GradleConfiguredValueContainer:<set-value>",
    )
}
