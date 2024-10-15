plugins {
    alias(libs.plugins.backintimeDebuggerFeature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // FIXME: feature modules should not depend on websocket server module.
            implementation(projects.debugTool.core.server)
            implementation(projects.core.websocket.server)
        }
    }
}
