plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.backintimeLint)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":backintime-runtime"))
            implementation(project(":backintime-websocket-event"))
            implementation(project(":backintime-websocket-server"))
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.datetime)
            compileOnly(libs.koin.annotations)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.test.junit)
            implementation(libs.koin.test)
            implementation(libs.kotlinx.coroutines.test)
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

sqldelight {
    databases {
        create("BackInTimeDatabase") {
            packageName.set("com.github.kitakkun.backintime.debugger.data")
        }
    }
}
