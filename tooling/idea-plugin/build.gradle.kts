plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(17)
}

repositories {
    intellijPlatform {
        defaultRepositories()
        intellijDependencies()
    }
    mavenCentral()
    google()
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(libs.versions.idea)
        bundledPlugin("com.intellij.java")
    }

    implementation(projects.core.websocket.server)
    implementation(projects.core.websocket.event)
    implementation(projects.tooling.app)
    implementation(projects.tooling.core.ui)
    implementation(projects.tooling.core.shared)
    implementation(projects.tooling.core.model)
    implementation(projects.tooling.core.database)
    implementation(projects.tooling.core.usecase)
    implementation(libs.jewel)
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
    }
}

// FYI: https://youtrack.jetbrains.com/issue/IJPL-1325/Classpath-clash-when-using-coroutines-in-an-unbundled-IntelliJ-plugin
tasks {
    run {
        // workaround for https://youtrack.jetbrains.com/issue/IDEA-285839/Classpath-clash-when-using-coroutines-in-an-unbundled-IntelliJ-plugin
        buildPlugin {
            exclude { "kotlinx.coroutines" in it.name }
        }
        prepareSandbox {
            exclude { "kotlinx.coroutines" in it.name }
        }
    }
}
