plugins {
    alias(libs.plugins.intelliJCommon)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    implementation(projects.tooling.core.model)
    implementation(projects.core.websocket.event)
    implementation(libs.kotlinx.serialization.json)
}
