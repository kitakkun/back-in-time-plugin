pluginManagement {
    includeBuild("build-logic")
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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "backintime"

include(
    "plugin-common",
    "gradle-plugin",
    "compiler",
    ":compiler-test",
    ":demo:app",
    ":core:runtime",
    ":core:annotations",
    ":core:websocket:server",
    ":core:websocket:client",
    ":core:websocket:event",
    ":debug-tool:data",
    ":debug-tool:app",
    ":debug-tool:feature:settings",
    ":debug-tool:feature:instance",
    ":debug-tool:feature:log",
    ":debug-tool:core:usecase",
    ":debug-tool:core:model",
    ":debug-tool:core:data",
    ":debug-tool:core:database",
    ":debug-tool:core:datastore",
    ":debug-tool:core:server",
    ":debug-tool:feature:connection",
    ":debug-tool:ui",
    ":debug-tool:featurecommon",
    ":debug-tool:resources",
)
