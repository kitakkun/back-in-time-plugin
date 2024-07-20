plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()
}

dependencies {
    commonMainImplementation(projects.debugTool.core.data)
    commonMainImplementation(projects.debugTool.core.database)
    commonMainImplementation(projects.core.websocket.server)
    commonMainImplementation(projects.core.websocket.event)
    commonMainImplementation(libs.kotlinx.coroutines.core)
    commonMainImplementation(libs.koin.core)
}
