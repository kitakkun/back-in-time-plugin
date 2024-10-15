import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.backintimeLint)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.debugTool.ui)
            implementation(projects.debugTool.core.usecase)
            implementation(projects.debugTool.core.model)
            implementation(projects.core.websocket.server)
            implementation(projects.core.websocket.event)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.preview)
            implementation(compose.materialIconsExtended)
            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeVM)
            compileOnly(libs.koin.annotations)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.rin)
        }
        jvmMain.dependencies {
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.desktop.components.splitPane)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspJvm", libs.koin.ksp.compiler)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_DEFAULT_MODULE", "false")
}
