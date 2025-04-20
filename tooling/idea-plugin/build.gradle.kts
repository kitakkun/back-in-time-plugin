import util.ideaPluginRepos

plugins {
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.intelliJJewel)
}

repositories {
    intellijPlatform {
        defaultRepositories()
        intellijDependencies()
    }
    ideaPluginRepos()
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(libs.versions.idea)
        bundledPlugin("com.intellij.java")
    }

    implementation(projects.core.websocket.server) {
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation(projects.core.websocket.event)
    implementation(projects.tooling.app)
    implementation(projects.tooling.core.ui)
    implementation(projects.tooling.core.shared)
    implementation(projects.tooling.core.model)
    implementation(projects.tooling.core.database)
    implementation(projects.tooling.core.usecase)
}
