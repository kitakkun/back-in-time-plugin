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
    ":plugin-common",
    ":gradle-plugin",
    ":compiler",
    ":compiler-test",
    ":demo:app",
    ":core:runtime",
    ":core:annotations",
    ":core:websocket:server",
    ":core:websocket:client",
    ":core:websocket:event",
)
