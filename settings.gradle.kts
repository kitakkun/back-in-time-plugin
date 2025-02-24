rootProject.name = "backintime"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("gradle-conventions-settings")
    includeBuild("gradle-conventions")
}

plugins {
    id("settings-conventions")
}

include(
    ":gradle-plugin",
    ":compiler:k2",
    ":compiler:backend",
    ":compiler:cli",
    ":compiler:yaml",
    ":compiler:common",
    ":compiler-test",
    ":demo:app",
    ":core:runtime",
    ":core:annotations",
    ":core:websocket:server",
    ":core:websocket:client",
    ":core:websocket:event",
    ":tooling:flipper-lib",
    ":tooling:idea-plugin",
    ":tooling:core:model",
    ":tooling:core:database",
    ":tooling:core:ui",
    ":tooling:core:usecase",
    ":tooling:core:shared",
    ":tooling:app",
    ":tooling:feature:inspector",
    ":tooling:feature:settings",
    ":tooling:feature:log",
    ":tooling:standalone",
)
