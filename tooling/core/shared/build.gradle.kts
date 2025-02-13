plugins {
    alias(libs.plugins.intelliJCommon)
}

dependencies {
    implementation(projects.tooling.core.model)
    implementation(projects.core.websocket.event)
}
