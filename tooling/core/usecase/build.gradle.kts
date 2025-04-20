plugins {
    alias(libs.plugins.intelliJCommon)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
}

dependencies {
    implementation(projects.tooling.core.shared)
    implementation(projects.tooling.core.database)
    implementation(projects.tooling.core.model)
    implementation(compose.runtime) {
        exclude(group = "org.jetbrains.kotlinx")
    }
}
