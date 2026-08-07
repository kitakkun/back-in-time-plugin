plugins {
    alias(libs.plugins.jetwhaleHostComposeModule)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    compilerOptions {
        // The split-pane component is still experimental upstream; the whole feature is built on it.
        optIn.add("org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi")
    }
}

dependencies {
    implementation(projects.jetwhalePlugin.host.core.model)
    implementation(projects.jetwhalePlugin.host.core.ui)
    implementation(projects.jetwhalePlugin.host.core.shared)
    implementation(projects.jetwhalePlugin.host.core.usecase)

    // Not among the host-provided Compose artifacts, so it ships with the plugin. Its own
    // dependencies (Compose runtime/foundation, kotlin-stdlib) are host-provided, hence
    // isTransitive = false: only the split-pane jar itself reaches the plugin jar.
    implementation(libs.jetwhaleComposeSplitPane) { isTransitive = false }

    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlin.test)
}
