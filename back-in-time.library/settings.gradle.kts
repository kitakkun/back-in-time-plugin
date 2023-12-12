pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
    plugins {
        id("com.android.library") version "8.2.0" apply false
        kotlin("multiplatform") version "1.9.21" apply false
        kotlin("plugin.serialization") version "1.9.21" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "back-in-time.library"
