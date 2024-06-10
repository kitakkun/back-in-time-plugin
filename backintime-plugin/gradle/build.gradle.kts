plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.backintimeLint)
    `java-gradle-plugin`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("backInTime") {
            id = "com.github.kitakkun.backintime"
            implementationClass = "com.github.kitakkun.backintime.BackInTimePlugin"
        }
    }
}

dependencies {
    implementation(project(":backintime-plugin:common"))
    implementation(libs.kotlin.gradle.plugin.api)
    implementation(libs.kotlinx.serialization.json)
    compileOnly(kotlin("gradle-plugin"))
}
