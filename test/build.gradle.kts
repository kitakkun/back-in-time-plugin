import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.backintimeLint)
    id("com.github.kitakkun.backintime") version "1.0.0"
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
                implementation(project(":backintime-library:runtime"))
                implementation(project(":backintime-library:annotations"))
                implementation(project(":backintime-library:websocket:server"))
                implementation(project(":backintime-library:websocket:event"))
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
    }
}

android {
    compileSdk = 34
    namespace = "com.github.kitakkun.backintime.test"
}

backInTime {
    enabled = true
    valueContainers {
        androidValueContainers()
        composeMutableStates()
        collections()

        container {
            className = "com/github/kitakkun/backintime/test/specific/GradleConfiguredValueContainerTest.GradleConfiguredValueContainer"
            captures = listOf("<set-value>", "update")
            getter = "<get-value>"
            setter = "<set-value>"
        }
    }
}

// publish required artifacts when performing sync on IDEA
tasks.prepareKotlinIdeaImport {
    dependsOn(":backintime-plugin:common:publishToMavenLocal")
    dependsOn(":backintime-plugin:compiler:publishToMavenLocal")
}

// publish required artifacts when compiling via ./gradlew
tasks.withType(KotlinCompile::class).all {
    dependsOn(":backintime-plugin:common:publishToMavenLocal")
    dependsOn(":backintime-plugin:compiler:publishToMavenLocal")
}
