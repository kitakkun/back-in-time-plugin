plugins {
    alias(libs.plugins.intelliJCommon)
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.kitakkun.backintime.tooling.core.database")
        }
    }
}

dependencies {
    implementation(projects.core.websocket.event)
    implementation(projects.tooling.core.model)
    implementation(projects.tooling.core.shared)

    implementation(libs.sqldelight.sqlite.driver)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.sqldelight.coroutines.extensions) {
        exclude(group = "org.jetbrains.kotlinx")
    }

    testImplementation(libs.kotlin.test)
}
