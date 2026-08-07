plugins {
    alias(libs.plugins.jetwhaleHostComposeModule)
}

dependencies {
    api(projects.jetwhalePlugin.host.core.shared)
    implementation(projects.jetwhalePlugin.host.core.model)
    implementation(projects.jetwhalePlugin.host.core.database)

    compileOnly(libs.kotlinx.coroutines.core)
}
