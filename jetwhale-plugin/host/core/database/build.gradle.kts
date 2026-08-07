plugins {
    alias(libs.plugins.jetwhaleHostModule)
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
    api(projects.jetwhalePlugin.host.core.shared)
    implementation(projects.jetwhalePlugin.host.core.model)
    implementation(projects.jetwhalePlugin.protocol)

    // SQLDelight is not among the host-provided libraries, so it ships inside the plugin jar.
    implementation(libs.sqldelight.sqlite.driver)
    implementation(libs.sqldelight.coroutines.extensions)

    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.coroutines.core)
}
