plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    google()
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
}

dependencies {
    implementation(projects.tooling.core.shared)
    implementation(projects.tooling.core.model)
    implementation(projects.tooling.core.database)
    implementation(projects.tooling.core.usecase)

    implementation(libs.jewel)
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
    }
    implementation(libs.kotlinx.serialization.json)
}
