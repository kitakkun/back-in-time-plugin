pluginManagement {
    includeBuild("build-logic")
    includeBuild("build-logic-for-testing")
    repositories {
        mavenLocal()
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
    ":backintime-plugin-common",
    ":backintime-gradle-plugin",
    ":backintime-compiler",
    ":backintime-runtime",
    ":backintime-annotations",
    ":test",
    ":backintime-demo",
    ":backintime-demo:app",
    ":backintime-websocket:server",
    ":backintime-websocket:client",
    ":backintime-websocket:event",
    ":backintime-websocket:test",
)
