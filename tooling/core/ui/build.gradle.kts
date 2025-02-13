plugins {
    alias(libs.plugins.intelliJJewel)
}

dependencies {
    implementation(projects.tooling.core.shared)
    implementation(libs.kotlinx.serialization.json)
}
