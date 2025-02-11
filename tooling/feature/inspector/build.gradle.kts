plugins {
    alias(libs.plugins.intelliJComposeFeature)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    implementation(projects.tooling.core.shared)
    implementation(projects.tooling.core.database)
    implementation(libs.kotlinx.serialization.json)
}
