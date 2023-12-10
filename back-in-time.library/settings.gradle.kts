pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
    plugins {
        id("com.android.library") version "8.1.0" apply false
        kotlin("multiplatform") version "1.9.0" apply false
        kotlin("plugin.serialization") version "1.9.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "back-in-time.library"
