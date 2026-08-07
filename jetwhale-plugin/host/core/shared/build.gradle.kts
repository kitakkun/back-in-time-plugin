plugins {
    alias(libs.plugins.jetwhaleHostModule)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    api(projects.jetwhalePlugin.host.core.model)

    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.coroutines.core)
}
