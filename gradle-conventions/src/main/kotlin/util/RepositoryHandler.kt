package util

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

fun RepositoryHandler.ideaPluginRepos() {
    mavenCentral()
    google()
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
}
