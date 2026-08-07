plugins {
    alias(libs.plugins.jetwhaleHostComposeModule)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    api(projects.jetwhalePlugin.host.core.shared)
    implementation(projects.jetwhalePlugin.host.core.model)

    // The split-pane splitter lives here so both features draw the same seam. Not among the
    // host-provided Compose artifacts, so it ships with the plugin; its own dependencies are
    // host-provided, hence isTransitive = false.
    implementation(libs.jetwhaleComposeSplitPane) { isTransitive = false }

    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.coroutines.core)
}
