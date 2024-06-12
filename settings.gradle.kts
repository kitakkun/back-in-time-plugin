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
    ":backintime-plugin:common",
    ":backintime-plugin:gradle",
    ":backintime-plugin:compiler",
    ":test",
    ":backintime-demo",
    ":backintime-demo:app",
    ":backintime-debugger:app",
    ":backintime-debugger:data",
    ":backintime-debugger:ui",
    ":backintime-debugger:featurecommon",
    ":backintime-debugger:feature:instance",
    ":backintime-debugger:feature:log",
    ":backintime-debugger:feature:connection",
    ":backintime-debugger:feature:settings",
    ":backintime-runtime",
    ":backintime-library:annotations",
    ":backintime-websocket-server",
    ":backintime-websocket-client",
    ":backintime-websocket-event",
    ":backintime-runtime",
    ":backintime-annotations",
    ":backintime-websocket-server",
    ":backintime-websocket-client",
    ":backintime-websocket-event",
)
