plugins {
    alias(libs.plugins.jetwhaleHostComposeModule)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    implementation(projects.jetwhalePlugin.host.core.model)
    implementation(projects.jetwhalePlugin.host.core.ui)
    implementation(projects.jetwhalePlugin.host.core.shared)
    implementation(projects.jetwhalePlugin.host.core.usecase)

    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlin.test)
}
