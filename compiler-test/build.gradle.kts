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
                implementation(projects.tooling.core.model)
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

    sourceSets.configureEach {
        languageSettings.enableLanguageFeature("ExplicitBackingFields")
    }

    compilerOptions {
        freeCompilerArgs.addAll("-P", "plugin:com.kitakkun.backintime.compiler:config=$projectDir/backintime-default-config.yaml")
    }
}

dependencies {
    kotlinCompilerPluginClasspath(projects.compiler.cli)
    kotlinNativeCompilerPluginClasspath(projects.compiler.cli)
}

android {
    compileSdk = 34
    namespace = "com.kitakkun.backintime.test"
}
