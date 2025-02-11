import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import util.libs

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

configure<KotlinJvmProjectExtension> {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    google()
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
}

dependencies {
    implementation(project(":tooling:core:ui"))
    implementation(project(":tooling:core:model"))
    implementation(project(":tooling:core:usecase"))
    implementation(libs.findLibrary("jewel").get())
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
    }
}
