plugins {
    alias(libs.plugins.jetwhaleHostModule)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    // kotlinx.serialization is provided by the JetWhale host at runtime.
    compileOnly(libs.kotlinx.serialization.json)
}
