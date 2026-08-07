plugins {
    alias(libs.plugins.jetwhaleHostComposeModule)
    // packagePlugin / installPlugin / stageDevPlugin / runJetWhale / runJetWhaleHot
    alias(libs.plugins.jetwhaleHost)
}

jetwhalePlugin {
    // "plugin" is too generic a name for ~/.jetwhale/plugins/, where every plugin jar lands together.
    pluginArchiveName.set("backintime")
    hostVersion.set(libs.versions.jetwhale.get())
}

// packagePlugin bundles everything on runtimeClasspath. The modules below reach it transitively --
// the protocol module is also published for agent-side consumers, where they are genuine runtime
// dependencies -- but the host already has them on the plugin classloader's parent, so bundling a
// second copy only inflates the jar (and risks two incompatible copies of the same class).
configurations.runtimeClasspath {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-serialization-core")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-serialization-core-jvm")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-serialization-json")
    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-serialization-json-jvm")
    exclude(group = "org.jetbrains.compose")
    exclude(group = "com.kitakkun.jetwhale")
}

dependencies {
    implementation(projects.jetwhalePlugin.protocol)
    implementation(projects.jetwhalePlugin.host.app)
    implementation(projects.jetwhalePlugin.host.core.shared)
    implementation(projects.jetwhalePlugin.host.core.model)
    implementation(projects.jetwhalePlugin.host.core.database)

    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.coroutines.core)
}
