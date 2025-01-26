plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.tooling.core.shared)
    implementation(projects.tooling.core.database)
    implementation(projects.tooling.core.model)
    implementation(compose.runtime)
}
