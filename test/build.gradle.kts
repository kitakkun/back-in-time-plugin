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
                implementation(libs.backintime)
                // coroutines
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.lifecycle.viewmodel.ktx)
                implementation(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.androidx.lifecycle.livedata.core.ktx)
                implementation(libs.androidx.compose.runtime)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.robolectric)
                implementation(libs.mockk)
            }
        }
        jvmTest {
            dependencies {
                implementation(libs.mockk)
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

backInTime {
    enabled = true
    valueContainers {
        androidValueContainers()
        composeMutableStates()
        collections()

        container {
            className = "com.github.kitakkun.backintime.test.GradleConfiguredValueContainer"
            captures = listOf("<set-value>", "update")
            getter = "<get-value>"
            setter = "<set-value>"
        }
    }
}
