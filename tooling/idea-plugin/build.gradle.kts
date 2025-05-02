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

    // need this for ktor to work properly
    implementation(libs.kotlinxIoCore)
}

tasks {
    run {
        // workaround for https://youtrack.jetbrains.com/issue/IDEA-285839/Classpath-clash-when-using-coroutines-in-an-unbundled-IntelliJ-plugin
        buildPlugin {
            exclude { "coroutines" in it.name && "kotlinx" in it.name }
        }
        prepareSandbox {
            exclude { "coroutines" in it.name && "kotlinx" in it.name }
        }
    }
}

intellijPlatform {
    publishing {
        val pluginVersion = libs.versions.backintime.get()
        pluginVersion.split("-").lastOrNull()?.let {
            when {
                it.startsWith("alpha") -> {
                    channels = listOf("alpha")
                }

                it.startsWith("beta") -> {
                    channels = listOf("beta")
                }
            }
        }
    }
}
