rootProject.name = "gradle-conventions"

pluginManagement {
    includeBuild("../gradle-conventions-settings")
}

plugins {
    id("settings-conventions")
}
