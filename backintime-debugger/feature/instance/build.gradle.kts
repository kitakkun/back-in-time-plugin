import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.debuggerFeature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":backintime-websocket-event"))
            implementation(libs.kotlinx.datetime)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.desktop.components.splitPane)
        }
    }
}
