plugins {
    alias(libs.plugins.jetwhaleHostComposeModule)
}

dependencies {
    api(projects.jetwhalePlugin.host.core.shared)
    implementation(projects.jetwhalePlugin.host.core.model)
    implementation(projects.jetwhalePlugin.host.core.ui)
    implementation(projects.jetwhalePlugin.host.core.usecase)
    implementation(projects.jetwhalePlugin.host.core.database)
    implementation(projects.jetwhalePlugin.host.feature.inspector)
    implementation(projects.jetwhalePlugin.host.feature.log)
    implementation(projects.jetwhalePlugin.host.feature.settings)

    compileOnly(libs.kotlinx.coroutines.core)
}
