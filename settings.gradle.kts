pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

rootProject.name = "backintime"

include(
    ":plugin-common",
    ":gradle-plugin",
    ":kotlin-plugin",
    ":library",
)
