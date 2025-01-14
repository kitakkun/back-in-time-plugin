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
    }
    mavenCentral()
    google()
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
}

dependencies {
    intellijPlatform {
        create("IC", "2024.3.1")
        bundledPlugin("com.intellij.java")
    }

    implementation(projects.core.websocket.server)
    implementation(projects.core.websocket.event)
    implementation(projects.tooling.shared)
    implementation(projects.tooling.model)
    implementation(projects.tooling.database)
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
