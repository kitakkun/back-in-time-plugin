import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.backintimeDebuggerFeature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.websocket.event)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
        }
        jvmMain.dependencies {
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.desktop.components.splitPane)
        }
    }
}
