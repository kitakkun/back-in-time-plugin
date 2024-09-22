plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.backintimeLint)
    alias(libs.plugins.javaGradlePlugin)
    alias(libs.plugins.mavenPublish)
}

gradlePlugin {
    plugins {
        create("backInTime") {
            id = "io.github.kitakkun.backintime"
            implementationClass = "io.github.kitakkun.backintime.BackInTimePlugin"
        }
    }
}

dependencies {
    implementation(projects.backintimePlugin.common)
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.kotlinx.serialization.json)
    compileOnly(kotlin("gradle-plugin"))
}
