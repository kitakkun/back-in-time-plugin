plugins {
    alias(libs.plugins.debuggerFeature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // FIXME: feature modules should not depend on websocket server module.
            implementation(project(":backintime-library:websocket:server"))
        }
    }
}
