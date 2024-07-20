plugins {
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm()

    sourceSets.commonMain.dependencies {
        implementation(compose.components.resources)
        implementation(compose.runtime)
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "io.github.kitakkun.backintime.debugger.resources"
    generateResClass = always
}
