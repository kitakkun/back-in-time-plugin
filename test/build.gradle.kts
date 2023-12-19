import com.github.kitakkun.backintime.BackInTimeExtension

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.github.kitakkun.backintime")
}

kotlin {
    jvmToolchain(17)
    jvm()
    androidTarget()

    sourceSets.all {
        languageSettings.languageVersion = "2.0"
    }

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
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")
            }
        }
        androidMain {
            dependencies {
                implementation("androidx.core:core-ktx:1.12.0")
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
                implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.6.2")
                implementation("androidx.compose.runtime:runtime-android:1.5.4")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("org.robolectric:robolectric:4.11.1")
                implementation("io.mockk:mockk:1.13.8")
            }
        }
        jvmTest {
            dependencies {
                implementation("io.mockk:mockk:1.13.8")
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
        "com.github.kitakkun.backintime.test.GradleConfiguredValueContainer:<set-value>",
        "com.github.kitakkun.backintime.test.GradleConfiguredValueContainer:update",
    )
    valueGetters += listOf(
        "com.github.kitakkun.backintime.test.GradleConfiguredValueContainer:<get-value>",
    )
    valueSetters += listOf(
        "com.github.kitakkun.backintime.test.GradleConfiguredValueContainer:<set-value>",
    )
}
