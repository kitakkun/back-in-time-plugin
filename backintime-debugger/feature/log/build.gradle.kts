import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.debuggerFeature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":backintime-library:websocket:event"))
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
        }
        jvmMain.dependencies {
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.desktop.components.splitPane)
        }
    }
}
