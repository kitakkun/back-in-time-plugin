plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.backintimeLint)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":backintime-debugger:ui"))
            implementation(project(":backintime-debugger:data"))
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
