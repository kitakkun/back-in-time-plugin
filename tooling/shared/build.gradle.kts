plugins {
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.tooling.model)
    implementation(libs.kotlinx.coroutines.core)
}
