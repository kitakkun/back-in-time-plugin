plugins {
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.tooling.core.model)
    implementation(projects.core.websocket.event)
    implementation(libs.kotlinx.coroutines.core)
}
