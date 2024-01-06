plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.backintime)
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
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
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

// publish required artifacts to local maven repository before evaluating test module
tasks.prepareKotlinIdeaImport {
    dependsOn(":backintime-core:publishToMavenLocal")
    dependsOn(":backintime-plugin-common:publishToMavenLocal")
    dependsOn(":backintime-compiler:publishToMavenLocal")
}
