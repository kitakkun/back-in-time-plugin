plugins {
    alias(libs.plugins.intelliJComposeFeature)
}

dependencies {
    implementation(projects.tooling.core.shared)
    implementation(projects.tooling.core.database)
    implementation(projects.tooling.feature.log)
    implementation(projects.tooling.feature.inspector)
    implementation(projects.tooling.feature.settings)
}
