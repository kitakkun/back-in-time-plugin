plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.websocket.event)
            implementation(projects.debugTool.core.database)
            implementation(projects.debugTool.core.data)
            implementation(projects.debugTool.core.datastore)
            implementation(projects.debugTool.core.model)
            implementation(compose.runtime)
            implementation(libs.koin.compose)
        }
    }
}
