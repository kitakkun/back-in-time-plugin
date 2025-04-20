import gradle.kotlin.dsl.accessors._4afc12a4163a87e8ad5956ccbdb3ac52.compileOnly
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
    // FIXME: Preview not working if compileOnly, change to implementation if you need.
    compileOnly(libs.findLibrary("kotlinx-coroutines-core-intellij").get())
}
