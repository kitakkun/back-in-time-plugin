import util.libs

/**
 * Convention for JetWhale host-plugin modules that carry Compose UI.
 *
 * Only the Compose *compiler* plugin is applied — deliberately not `org.jetbrains.compose`, whose
 * version is pinned to what the IntelliJ tooling modules need. The Compose runtime the plugin runs
 * against is the one the JetWhale host ships, so those artifacts are declared explicitly and
 * `compileOnly`: bundling them would put a second copy of Compose in the plugin's classloader.
 */
plugins {
    id("jetwhale-host-module")
    id("org.jetbrains.kotlin.plugin.compose")
}

dependencies {
    listOf(
        "jetwhaleComposeRuntime",
        "jetwhaleComposeFoundation",
        "jetwhaleComposeUi",
        "jetwhaleComposeDesktop",
        "jetwhaleComposeMaterial3",
        // @Preview only matters to the IDE; an absent annotation class is ignored by the JVM.
        "jetwhaleComposeUiToolingPreview",
        // Bundling this would add ~37 MB to the plugin jar. The host application ships it, and the
        // plugin classloader delegates to the host's classpath first, so compileOnly is enough --
        // at the cost of a dependency on the host continuing to bundle it.
        "jetwhaleComposeMaterialIconsExtended",
    ).forEach { alias ->
        "compileOnly"(libs.findLibrary(alias).get())
        // The Compose compiler plugin refuses to run without the runtime on the compile classpath,
        // and compileOnly does not reach the test compilation. Test configurations are not part of
        // runtimeClasspath, so this cannot leak into the packaged plugin jar.
        "testImplementation"(libs.findLibrary(alias).get())
    }
}
