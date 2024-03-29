pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic-for-testing"

include(":backintime-plugin-common")
include(":backintime-gradle-plugin")
project(":backintime-plugin-common").projectDir = file("../backintime-plugin-common")
project(":backintime-gradle-plugin").projectDir = file("../backintime-gradle-plugin")
