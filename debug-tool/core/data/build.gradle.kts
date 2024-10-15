plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.debugTool.core.database)
            implementation(projects.debugTool.core.model)
            implementation(projects.core.websocket.event)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.uuid)
            implementation(libs.koin.core)
        }
    }
}
