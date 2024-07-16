import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.backintimeLint)
    id("io.github.kitakkun.backintime") version "1.0.0"
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
                implementation(project(":backintime-runtime"))
                implementation(project(":backintime-annotations"))
                implementation(project(":backintime-websocket-server"))
                implementation(project(":backintime-websocket-event"))
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
                implementation(libs.mockk)
            }
        }
    }
}

android {
    compileSdk = 34
    namespace = "io.github.kitakkun.backintime.test"
}

backInTime {
    enabled = true
}

// publish required artifacts when performing sync on IDEA
tasks.prepareKotlinIdeaImport {
    dependsOn(":backintime-plugin:common:publishToMavenLocal")
    dependsOn(":backintime-plugin:compiler:publishToMavenLocal")
}

// publish required artifacts when compiling via ./gradlew
tasks.withType<KotlinCompilationTask<*>>().all {
    dependsOn(":backintime-plugin:common:publishToMavenLocal")
    dependsOn(":backintime-plugin:compiler:publishToMavenLocal")
}
