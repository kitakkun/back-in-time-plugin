pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
    plugins {
        kotlin("multiplatform") version "1.9.0" apply false
        id("com.android.library") version "8.1.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
