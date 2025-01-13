plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvmToolchain(17)
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.kitakkun.backintime.tooling.database")
        }
    }
}

dependencies {
    implementation(projects.core.websocket.event)
    implementation(projects.tooling.model)
    implementation(projects.tooling.shared)
    implementation(libs.sqldelight.sqlite.driver)
    implementation(libs.sqldelight.coroutines.extensions)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlin.test)
}
