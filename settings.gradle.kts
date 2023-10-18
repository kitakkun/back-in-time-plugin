pluginManagement {
    repositories {
        mavenCentral()
    }
    plugins {
        kotlin("jvm") version "1.9.0" apply false
        kotlin("kapt") version "1.9.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "back-in-time"
include(":gradle-plugin")
include(":kotlin-plugin")
include(":annotations")
include(":runtime")
