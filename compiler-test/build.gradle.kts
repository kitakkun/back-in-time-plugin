plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.backintimeLint)
}

kotlin {
    jvmToolchain(17)
    jvm()
    androidTarget()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.runtime)
                implementation(projects.core.annotations)
                implementation(projects.core.websocket.server)
                implementation(projects.core.websocket.event)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.test.junit)
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
            }
        }
    }
}

dependencies {
    kotlinCompilerPluginClasspath(projects.compiler)
    kotlinNativeCompilerPluginClasspath(projects.compiler)
}

android {
    compileSdk = 34
    namespace = "io.github.kitakkun.backintime.test"
}
