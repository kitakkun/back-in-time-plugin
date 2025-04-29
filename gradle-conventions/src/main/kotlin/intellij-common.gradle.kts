import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import util.ideaPluginRepos
import util.libs

plugins {
    id("org.jetbrains.kotlin.jvm")
}

configure<KotlinJvmProjectExtension> {
    jvmToolchain(17)
}

repositories {
    ideaPluginRepos()
}

dependencies {
    implementation(libs.findLibrary("kotlinx-coroutines-core-intellij").get())
}
